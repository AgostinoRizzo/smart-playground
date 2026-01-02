#!/usr/bin/python3

import cv2
import cv2.aruco as aruco
import numpy as np

ARUCO_DICT = aruco.Dictionary_get(aruco.DICT_4X4_50)
ARUCO_PARAMETERS = aruco.DetectorParameters_create()
capture = None

def init_camera_capture():
	global capture
	capture = cv2.VideoCapture(0)
	capture.set(cv2.CAP_PROP_EXPOSURE, -100)
	
	#cap.set(cv2.CAP_PROP_FRAME_WIDTH, 1000)
	#cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 1000)
	
	return capture.get(cv2.CAP_PROP_FRAME_WIDTH), capture.get(cv2.CAP_PROP_FRAME_HEIGHT)

def finalize_camera_capture():
	global capture
	capture.release()
	
def detect_aruco_markers(img, show=False):
	global ARUCO_DICT
	global ARUCO_PARAMETERS
	
	gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
	corners, ids, _ = aruco.detectMarkers(gray, ARUCO_DICT, parameters = ARUCO_PARAMETERS)
	
	if len(corners) and show:
		for square_corners in corners:
			for corner in square_corners[0]:
				print("Detected corner: %d, %d" % (corner[0], corner[1]))
				img = cv2.circle(img, (corner[0], corner[1]), 7, (255, 0, 0), -1)
		cv2.imshow('map', img)
	
	return corners, ids

def compute_aruco_marker_center(corners):
	center = corners[0] + corners[1] + corners[2] + corners[3]
	center[:] = [int(x / 4) for x in center]
	return center

def detect_capture_markers():
	global capture
	ret, frame = capture.read()
	return detect_aruco_markers(frame)


def main():
	print("START VIDEO CAPTURE")
	cap = cv2.VideoCapture(0)
	print("VIDEO CAPTURE STARTED")
	cap.set(cv2.CAP_PROP_EXPOSURE, -100)

	#cap.set(cv2.CAP_PROP_FRAME_WIDTH, 1000)
	#cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 1000)

	print("Image resolution (wxh): %dx%d" % (cap.get(cv2.CAP_PROP_FRAME_WIDTH), cap.get(cv2.CAP_PROP_FRAME_HEIGHT)))
	print("FPS: %d" % (cap.get(cv2.CAP_PROP_FPS)))
	print("Exposure: %d" % (cap.get(cv2.CAP_PROP_EXPOSURE)))
	print("ISO speed: %d" % (cap.get(cv2.CAP_PROP_ISO_SPEED)))

	while True:
		ret, frame = cap.read()
		
		detect_aruco_markers(frame, show=True)
		cv2.imshow('frame', frame)
		if cv2.waitKey(1) & 0xFF == ord('q'):
			break

	cap.release()
	cv2.destroyAllWindows()

if __name__ == '__main__':
	main()

