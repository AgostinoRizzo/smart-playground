Smart Playground
===

The IoT project features a collection of *smart objects* employed to create a *smart playground* for playing games such as *tennis* and *golf*.
Specifically, the project includes a **smart field**, a **smart ball**, a controller to be used as a **smart racket** and **smart club**, an **artificial smart racket**, and a **motion controller**.
The set of smart objects that make up the smart field offer a series of generic services, forming the interface through which different games can be implemented such as tennis, golf, bowling, etc.
For example, the smart ball provides generic movement services within the field that can be reused for the implementation of different games; the motion controller monitors the player's movement and activity (number of steps taken, calories burned, distance traveled), and his orientation with respect to the playing field.
Concerning the smart field, this offers monitoring of atmospheric conditions (brightness, temperature, humidity, wind speed and direction), as well as the ability to control the lighting and cooling system.
The project involves the use of the **telosb** wireless sensors (via **TinyOS** and **nesC**) used in the implementation of the smart field and the smart ball.
Other technologies were used to implement other modules:
*   **Arduino** for direct control of the lighting and cooling system;
*   the **Wiimote** controller for the implementation of the smart racket and the smart club;
*   an **Android app** for the tracking system of the smart ball and the objects within the field;
*   another **Android app** for monitoring the player's movement and orientation;
*   a **JavaFX application** for the implementation of a console through which it is possible to view, in real time and remotely, the status of the smart field as well as set some parameters and start the
games;
*   a Python software running on a **Raspberry Pi** for the implementation of the base station as well as the communication and interaction of all the developed modules.

The project was realized in the context of the *Programming Techniques for Embedded Systems and Sensor Networks* course during the bachelor's degree in Computer Science.

<table>
    <tr>
        <td width="50%">
            <table>
            <tr><img alt="Smart Playground" title="Smart Playground" src="./Figures/playground.png"></tr>
            <tr><img alt="Smart Field Tracking" title="Smart Field Tracking" src="./Figures/Pictures/smartfield_tracking.jpg"></tr>
            </table>
        </td>
        <td width="49%">
            <table>
            <tr><img alt="Console Dashboard" title="Console Dashboard" src="./Figures/Screenshots/console_dashboard.png"></tr>
            <tr><img alt="Console Play Tennis" title="Console Play Tennis" src="./Figures/Screenshots/console_tennis_window.png"></tr>
            <tr><img alt="Console Play Golf" title="Console Play Golf" src="./Figures/Screenshots/console_golf_window.png"></tr>
            </table>
        </td>
    </tr>
</table>

The following are various UML models and project architecture diagrams, with a detailed explanation of the various modules and their interaction.


### Smart Playground Modeling
The smart playground system is a collection of smart objects such as a ball, a field, and a racket.
For each of the created smart objects, as well as for the entire playground system, an UML class diagram describes its components and the offered services according to the following *meta-model*:

![Meta-Model](./Figures/meta_model.png "Meta-Model")

By instantiating the meta-model, a specific model is obtained for each of the realized smart objects:

![SmartPlayground Class Diagram](./Figures/smartplayground_class_diagram.png "SmartPlayground Class Diagram")
---
![SmartBall Class Diagram](./Figures/smartball_class_diagram.png "Smartball Class Diagram")
---
![SmartField Class Diagram](./Figures/smartfield_class_diagram.png "SmartField Class Diagram")

The following *use case diagram* reports the main functions a player (main actor) can take:

![Use Case Diagram](./Figures/use_case_diagram.png "Use Case Diagram")



### Architecture and Modules
The following diagrams show the system architecture at different levels of detail.
A box-and-line diagram is provided, showing the breakdown of the entire system into subsystems and modules as well as the services offered by each structural unit.
A deployment diagram then shows how the individual structural units are distributed across the system's individual hardware units, and the methods and technologies used to communicate with each other.
Finally, a low-level deployment diagram is provided to show all the individual hardware units and the used technologies.

![Box-and-Line Architecture](./Figures/boxline_architecture.png "BoxAndLine Architecture")
---
![High-Level Deployment Architecture](./Figures/highlevel_deployment_architecture.png "High-Level Deployment Architecture")
---
![Low-Level Deployment Architecture](./Figures/lowlevel_deployment_architecture.png "Low-Level Deployment Architecture")
---
![Layer Architecture](./Figures/layer_architecture.png "Layer Architecture")



### Smart Ball (TinyOS/NesC Components)
The smart ball object is entirely based on the *telosb* board.
This implements the business logic related to the movement of the ball within the field.
Specifically, the board's software:
*   communicates with the base station (802.15.4 radio link);
*   detects the values ​​of the brightness, temperature, and humidity sensors to monitor the environmental conditions of the field (necessary for controlling the cooling and lighting system);
*   manages the motor control by communicating with the *L298N driver*;
*   detects the boundaries of the field through the four brightness sensors, located underneath the structure, to allow it to "bounce" and prevent it from leaving the field.

![Smart Ball](./Figures/smartball.png "Smart Ball")



### Smart Field (TinyOS/NesC Components)
The smart field object is based on the *telosb* board.
This takes the role of a base station for the communication with the smart ball (802.15.4 radio link) by implementing the forwarding with the rest of the system, specifically with the Raspberry Pi via the UART link.

![Smart Field (telosb)](./Figures/smartfield_telosb.png "Smart Field (telosb)")

#### Wind Sensor
The wind sensor is connected to the smart field's telosb board.
This is achieved using a brightness sensor, placed under the propeller blades, to detect the intensity, and a potentiometer to detect the direction.
An appropriate interface and a NesC component have been implemented to manage the sensor.

![Wind Sensor](./Figures/wind_sensor.png "Wind Sensor")

#### Cooling and Lighting System
The data related to the monitoring of brightness, temperature, and humidity sensors (from the smart ball and the smart field) are collected to allow the control of the cooling and lighting system.
A brightness level below a certain threshold triggers the activation of the LEDs located in the four quadrants of the field.
They will activate in sync with the ball's position within the field (if the ball is within the field, only the LED corresponding to the quadrant in which it is located will activate; otherwise, if the ball is outside the field, the LEDs in all four quadrants will activate).
Similarly, temperature and humidity levels above a certain threshold trigger the activation of the fans, also located in the four quadrants of the field.
They will activate in sync with the ball's position within the field.
To implement the cooling and lighting system, it has been decided to use a dedicated Arduino board to control the 4 LEDs and 4 fans inside the structure.
The telosb sensor will communicate with the Arduino board, indicating the *on* and *off* modes.

![Cooling and Lighting System](./Figures/cooling_lighting_system.png "Cooling and Lighting System")

Below are some diagrams of the TinyOS/NesC components for the smart ball's and smart field's telosb board.
Specifically, separate components have been developed for each of the offered services and the used external sensors.

![Smart Ball nesC Components](./Figures/smartball_nesC_components.png "Smart Ball nesC Components")
---
![Smart Field nesC Components](./Figures/smartfield_nesC_components.png "Smart Field nesC Components")



### Object Tracking System
To detect the field boundaries, and track the smart ball's position and orientation with respect to these boundaries, an Android app is provided, running on a smartphone positioned on the top of the field's structure.
The entire field is then framed by the smartphone's camera, allowing the app to analyze the resulting images using the [OpenCV](https://opencv.org/) computer vision library.
Specifically, object tracking within the field occurs through the recognition of special markers, called *ArUco*, placed on the objects to be tracked.
An ArUco marker is a square marker composed of a wide black border and an internal binary matrix that determines its identifier.
The black border facilitates a rapid detection in the image.
Below are some examples of ArUco markers used on the smart ball and at the four corners of the field:

<table>
    <tr>
        <td><img alt="ArUco 0" title="ArUco 0" src="./figures/ArUco/aruco_0.png"></td>
        <td><img alt="ArUco 1" title="ArUco 1" src="./figures/ArUco/aruco_1.png"></td>
        <td><img alt="ArUco 2" title="ArUco 2" src="./figures/ArUco/aruco_2.png"></td>
        <td><img alt="ArUco 3" title="ArUco 3" src="./figures/ArUco/aruco_3.png"></td>
        <td><img alt="ArUco 4" title="ArUco 4" src="./figures/ArUco/aruco_4.png"></td>
        <td><img alt="ArUco 5" title="ArUco 5" src="./figures/ArUco/aruco_5.png"></td>
    </tr>
</table>

The detection of each marker, by the OpenCV library, determines the position (*left* and *top* coordinates) of the 4 corners.
Therefore the position of the object corresponds to the center of the marker along with its direction vector according to the following scheme:

![ArUco Single Marker](./Figures/aruco_single_marker.png "ArUco Single Marker")

Specifically, four markers are placed at the four corners of the field to identify its boundaries and thus the positioning of objects within them.
Each object, such as the smart ball and the golf hole, will then be equipped with a specific marker, allowing it to be tracked relative to the field boundaries.

#### Ball Tracking Speed-Up
Object tracking through the ArUco marker recognition presents a flaw, especially when objects are moving such as the smart ball.
When the marker is placed on a moving object, the image blurring causes it to be unrecognized, resulting in a loss of the object tracking.
To mitigate this problem, a dual tracking strategy was adopted for moving objects.
In addition to the ArUco marker recognition, which continues to be used for the detection of the object's direction, a faster color-based recognition of the position is added.
Each object will then be assigned a specific color so that its position, relative to the field's boundaries, can be recognized.
Color-based recognition does not suffer from the blurring caused by the object's motion, allowing it to be tracked even in this case.

<table>
    <tr>
        <td><img alt="Smart Ball Tracking" title="Smart Ball Tracking" src="./Figures/Pictures/smartball_tracking.jpg"></td>
        <td><img alt="Golf Hole Tracking" title="Golf Hole Tracking" src="./Figures/Pictures/golfhole_tracking.jpg"></td>
    </tr>
</table>

![Smart Field Tracking](./Figures/Pictures/smartfield_tracking.jpg "Smart Field Tracking")

The image above shows the placement of four ArUco markers at the four corners of the field, allowing for boundary detection.
Tracking of the golf hole and the smart ball is also shown, with both tracking strategies applied (color-based for position and marker-based for orientation).

Finally, object tracking data is sent to the Raspberry Pi board for use by the business logic (specific games such as tennis or golf) and to the `Console` module for real-time visualization.
Data is sent via the UDP protocol over a TCP/IP Wi-Fi link.


### Playground Console
The entire system can be controlled from the `Console` module.
This is a JavaFX desktop application that allows the user to view the status of the smart playground, including graphs of brightness, temperature, and humidity values ​​from the two telosbs corresponding to the smart field and the smart ball (a specific graph is shown for each sensor; an additional graph shows an aggregation of the sensor values such as the average).
The application also features:
*   the visualization of the wind status (intensity and direction);
*   the positioning of objects (smart ball and golf hole) within the field (data from the tracking system);
*   graphs about to the Wiimote sensors (smart racket and smart club);
*   the player's movement status, including the number of steps taken, total distance traveled, and calories burned (data from the `MotionController` module).

In addition, the `Console` module allows to control the court's cooling and lighting system, as well as the configuration of each individual game (number of matches/game sets, artificial player level, hole positioning, change of the instrument used such as racket and clubs). A real-time display of the status and score of the played matches is provided.

![Console Dashboard](./Figures/Screenshots/console_dashboard.png "Console Dashboard")
![Console Field Status](./Figures/Screenshots/console_field.png "Console Field Status")
![Console Play Game](./Figures/Screenshots/console_play.png "Console Play Game")
![Console Play Tennis](./Figures/Screenshots/console_tennis.png "Console Play Tennis")
![Console Play Golf](./Figures/Screenshots/console_golf.png "Console Play Golf")
