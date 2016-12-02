
import base64
import os
import sys
import math
from PIL import Image
from PIL import ImageDraw
from PIL import ImageFont
from difflib import SequenceMatcher

import cv2
import numpy as np
import matplotlib.pyplot as plt

from googleapiclient import discovery 
from googleapiclient import errors
from oauth2client.client import GoogleCredentials

DISCOVERY_URL = 'https://{api}.googleapis.com/$discovery/rest?version={apiVersion}'  # noqa
os.environ['GOOGLE_APPLICATION_CREDENTIALS']='saurabh_google_api_credential.json'


class VisionApi:
    """Construct and use the Google Vision API service.
	----- Usage
	vision = VisionApi()
	vision.detect_text_and_output_cropped_image('language_images/written_word_2_ball.jpg', 'ball', 'correct_new.jpg')
	or
	vision.detect_text_and_output_cropped_image('language_images/written_word_2_ball.jpg', 'ball')
    """
    def __init__(self, api_discovery_file='vision_api.json'):
        self.credentials = GoogleCredentials.get_application_default()
        self.service = discovery.build(
            'vision', 
            'v1', 
            credentials=self.credentials,
            discoveryServiceUrl=DISCOVERY_URL
        )

    def detect_text(self, input_filenames, num_retries=3, max_results=6):
        """Uses the Vision API to detect text in the given file.
        """
        images = {}
        for filename in input_filenames:
            print(filename)
            assert(os.path.exists(filename))
            with open(filename, 'rb') as image_file:
                images[filename] = image_file.read()

        batch_request = []
        for filename in images:
            batch_request.append({
                'image': {
                    'content': base64.b64encode(images[filename]).decode('UTF-8')
                },
                'features': [{
                    'type': 'TEXT_DETECTION',  #Tells Vision API that we're making a request to do OCR, else 'LABEL_DETECTION'
                    'maxResults': max_results,
                }]
            })
        request = self.service.images().annotate(body={'requests': batch_request})

        try:
            responses = request.execute(num_retries=num_retries)
            if 'responses' not in responses:
                return {}
            text_response = {}
            for filename, response in zip(images, responses['responses']):
                if 'error' in response:
                    print("API Error for %s: %s" % (
                            filename,
                            response['error']['message']
                            if 'message' in response['error']
                            else ''))
                    continue
                if 'textAnnotations' in response:
                    text_response[filename] = response['textAnnotations']
                else:
                    text_response[filename] = []
            return text_response
        except errors.HttpError as e:
            print("Http Error for %s: %s" % (filename, e))
        except KeyError as e2:
            print("Key error: %s" % e2)

    def detect_text_from_image(self, input_image, num_retries=3, max_results=6):
        """Uses the Vision API to detect text in the given file.
        """
        batch_request = []
        batch_request.append({
            'image': {
                'content': base64.b64encode(input_image).decode('UTF-8')
            },
            'features': [{
                'type': 'TEXT_DETECTION',  #Tells Vision API that we're making a request to do OCR, else 'LABEL_DETECTION'
                'maxResults': max_results,
            }]
        })
        request = self.service.images().annotate(body={'requests': batch_request})

        try:
            responses = request.execute(num_retries=num_retries)
            if 'responses' not in responses:
                return {}
            text_response = []
            for response in responses['responses']:
                if 'error' in response:
                    print("API Error : %s" % (
                            response['error']['message']
                            if 'message' in response['error']
                            else ''))
                    continue
                if 'textAnnotations' in response:
                    text_response.append(response['textAnnotations'])
                else:
                    text_response.append([])
            return text_response
        except errors.HttpError as e:
            print("Http Error for %s: %s" % (filename, e))
        except KeyError as e2:
            print("Key error: %s" % e2)        

    def detect_label(self, input_filenames, num_retries=3, max_results=10):
        """Uses the Vision API to detect text in the given file.
        """
        images = {}
        for filename in input_filenames:
            print(filename)
            assert(os.path.exists(filename))
            with open(filename, 'rb') as image_file:
                images[filename] = image_file.read()

        batch_request = []
        for filename in images:
            batch_request.append({
                'image': {
                    'content': base64.b64encode(images[filename]).decode('UTF-8')
                },
                'features': [{
                    'type': 'LABEL_DETECTION',  #Tells Vision API that we're making a request to do OCR, else 'LABEL_DETECTION'
                    'maxResults': max_results,
                }]
            })
        request = self.service.images().annotate(body={'requests': batch_request})

        try:
            responses = request.execute(num_retries=num_retries)
            if 'responses' not in responses:
                return {}
            label_response = {}
            for filename, response in zip(images, responses['responses']):
                if 'error' in response:
                    print("API Error for %s: %s" % (
                            filename,
                            response['error']['message']
                            if 'message' in response['error']
                            else ''))
                    continue
                if 'labelAnnotations' in response:
                    label_response[filename] = response['labelAnnotations']
                else:
                    label_response[filename] = []
            return label_response
        except errors.HttpError as e:
            print("Http Error for %s: %s" % (filename, e))
        except KeyError as e2:
            print("Key error: %s" % e2)

    def highlight_texts(self, image_filename, text_detection_response, expected_text, output_filename=None):
        """Draws a polygon around the faces, then saves to output_filename.
        Args:
          image: a file containing the image with the faces.
          faces: a list of faces found in the file. This should be in the format
              returned by the Vision API.
          output_filename: the name of the image file to be created, where the
              faces have polygons drawn around them.
        """
        if text_detection_response == [] or text_detection_response == None:
            return
        im = Image.open(image_filename)
        draw = ImageDraw.Draw(im)

        # Zeroth response is just an aggregation of all the responses.
        # So not considering it
        num_text_regions = len(text_detection_response) - 1
        similarity_ratio = [0.0]*num_text_regions
        print('############## ', image_filename)
        for i, text_region in enumerate(text_detection_response[1:]):
            text = text_region['description']
            similarity_ratio[i] = SequenceMatcher(None, expected_text.upper(), text.upper()).ratio()
            print(i, text, expected_text, similarity_ratio)

        best_match_similarity = max(similarity_ratio)
        best_match_index = similarity_ratio.index(best_match_similarity)

        text_region = text_detection_response[best_match_index + 1]
        box = [(v.get('x', 0.0), v.get('y', 0.0)) for v in text_region['boundingPoly']['vertices']]
        print(box)
        if best_match_similarity == 1:
            color = '#00ff00'
        else:
            font_size = 80
            font = ImageFont.truetype("calibri.ttf", font_size)
            text_area = (box[0][0], box[0][1] - font_size - 10)
            draw.text(text_area, expected_text, (0,255,0), font=font)
            color = '#ff0000'
        draw.line(box + [box[0]], width=5, fill=color)
        im.save(output_filename)

    def crop_and_highlight_texts(self, image_filename, text_detection_response, expected_text, output_filename=None):
        """Draws a polygon around the faces, then saves to output_filename.
        Args:
          image: a file containing the image with the faces.
          faces: a list of faces found in the file. This should be in the format
              returned by the Vision API.
          output_filename: the name of the image file to be created, where the
              faces have polygons drawn around them.
        """
        if text_detection_response == [] or text_detection_response == None:
            return
        im = Image.open(image_filename)
        draw = ImageDraw.Draw(im)

        # Zeroth response is just an aggregation of all the responses.
        # So not considering it
        num_text_regions = len(text_detection_response) - 1
        similarity_ratio = [0.0]*num_text_regions
        print('############## ', image_filename)
        for i, text_region in enumerate(text_detection_response[1:]):
            text = text_region['description']
            similarity_ratio[i] = SequenceMatcher(None, expected_text.upper(), text.upper()).ratio()
            print(i, text, expected_text, similarity_ratio)

        best_match_similarity = max(similarity_ratio)
        best_match_index = similarity_ratio.index(best_match_similarity)

        text_region = text_detection_response[best_match_index + 1]
        box = [(v.get('x', 0.0), v.get('y', 0.0)) for v in text_region['boundingPoly']['vertices']]
        print(box)
        print(box + [box[0]])
        print(type(im), type(draw))
        if best_match_similarity == 1:
            color = '#00ff00'
            font_size = 0
        else:
            font_size = 80
            font = ImageFont.truetype("calibri.ttf", font_size)
            text_area = (box[0][0], box[0][1] - font_size - 10)
            draw.text(text_area, expected_text, (0,255,0), font=font)
            color = '#ff0000'
        draw.line(box + [box[0]], width=5, fill=color)
        
        box_array = np.asarray(box).reshape(4,2)
        minArgIndex = np.argmin(box_array, axis=0)
        maxArgIndex = np.argmax(box_array, axis=0)
        print(box_array)
        print('minArgIndex ', minArgIndex)
        print('maxArgIndex ', maxArgIndex)
        ### This logic is not clean: it assumes the paper is rotated in a specific way - this can be taken care of though by having checks
        ## Or it might make more sense to rotate it, crop it, then find contours - atleast now we know the paper has no rotation !
        top_left     = box_array[minArgIndex[0]]
        bottom_right = box_array[maxArgIndex[0]]

        top_right    = box_array[minArgIndex[1]]
        bottom_left  = box_array[maxArgIndex[1]]

        print('top_left ' ,top_left)
        print(bottom_right)

        print(top_right)
        print(bottom_left)

        #    im.crop(box)
        margin = 10
        cropped_im = im.crop((box_array[minArgIndex[0]][0] - margin, box_array[minArgIndex[1]][1] - 2*margin - font_size, 
                              box_array[maxArgIndex[0]][0] + margin, box_array[maxArgIndex[1]][1] + margin))
        cropped_im.save(output_filename)

    def detect_text_and_output_cropped_image(self, image_filename, expected_text, output_filename=None):
        responses = self.detect_text([image_filename])
        if not output_filename:
            output_filename = '.'.join(['_'.join([image_filename.split('.')[0], 'output']), 'jpg'])
        self.crop_and_highlight_texts(image_filename, responses[image_filename], expected_text, output_filename)
            