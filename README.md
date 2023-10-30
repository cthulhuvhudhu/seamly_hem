# Seamly Hem : An Exploration of Digital Image Processing (DIP) in Kotlin

This follows the project guidelines by HyperSkill/JetBrains for Kotlin.

## Stage One: Drawing simple shapes
As an introductory, this will prompt the user for width, height, and image name; 
and generate a black rectangle with a red "X".

## Stage Two: Inverting images
This accepts two parameters via the command line:

> -in inputFileName -out outputFileName

The input image will be read, colors inverted, and stored in the output file.

## Configuration

Output and input directory for the image is configurable with:
> gradle run -PoutputDir=custom_output -PinputDir=custom_input