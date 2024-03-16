# Seamly Hem : An Exploration of Digital Image Processing (DIP) using Kotlin

This follows the project guidelines by HyperSkill/JetBrains for Kotlin for a popular advanced coding puzzle.

## Project Stages

### Stage One: Drawing simple shapes

As an introductory, this will prompt the user for width, height, and image name; 
and generate a black rectangle with a red "X".

### Stage Two: Inverting images

The input image will be read, colors inverted, and stored in the output file.

### Stage Three: Image energy

For each pixel the energy is calculated, as the square root of the squared difference 
of each pixel to the left and right, plus the squared difference of each pixel above and below.

> energy = sqrt((left-right)<sup>2</sup> + (up-down)<sup>2</sup>)

For pixels on the borders, the same calculation is used as the pixel adjacent in the same axis as calculation.
In other words, shift the "center" for the calculation one pixel away from each adjacent border and calculate 
from there.

> energy(0,1) = sqrt(((0,1)-(2,1))<sup>2</sup> + ((0,0)-(0,2))<sup>2</sup>)

Once the energy has been calculated for each pixel, we normalize each pixel relative to the maximum value,
and this is saved in the output image.

#### But what does energy mean?

Energy is the mathematical representation of contrast surrounding a pixel. The energy of pixels around borders,
objects, etc will be high. This maps loosely to "importance" of the pixel in maintaining the sharpness and context
relative to the image.

#### Motivation

By marking how "important" pixels are, we can do advanced image processing techniques, as we'll see later on.

### Stage Four: Vertical (and Horizontal) Seams

The goal of this stage is to identify a seam of pixels along one axis, whose sum of energy values is minimal of 
all possible candidates. The result is a line of pixels, one per row (or column).

To calculate this, each row can be iterated over using dynamic programming principles. Each pixel has three 
potential "parent" pixels. If at each row we store the optimal path to reach that pixel, we can simply choose 
the parent with the optimal number, as though each row was the terminal row.

#### Motivation

If we have an image that is too big for our application and need to trim it down, we can run into issues by simply
cutting the "ends" until it fits. What if there's something important on the ends, but dead space in the middle?
Minimal energy seams allow us to remove areas of a picture that are most similar to its neighbors, and therefore
we have a better chance of trimming the photo and maintaining visually important elements.

### Stage Five: Trimming pictures

In this stage energy, vertical, and horizontal seams are iteratively calculated and removed from the source 
picture to render a final picture with the desired dimensions.

## Configuration

Output and input directory for the image is configurable with:
> gradle run -PoutputDir=custom_output -PinputDir=custom_input

## Run

> --in inputFileName --out outputFileName --xWidth 125 --xHeight 150

Where `xWidth` is the number of pixels to reduce in the width, and `xHeight` is the number to reduce in the image height.