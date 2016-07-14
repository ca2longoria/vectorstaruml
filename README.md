vectorstaruml
=============

Convert StarUML's diagram uml file to an SVG vector image file.

The old code (2013) is being phased out.  Thanks to the discovery of **WhiteStarUML**, I've begun considering taking this project back up.  Perhaps under a python framework, this time.

#### Latest Progress
##### [test.8ed1b6d.svg](https://s3.amazonaws.com/ca2longoria-github-assets/vectorstaruml/test.8ed1b6d.svg) vs. [spritely-diagram.gif](https://f.cloud.github.com/assets/387353/1748170/ddb7fc5c-64a4-11e3-8746-f4e66fa1612b.gif) from uml file [spritely.uml](https://s3.amazonaws.com/ca2longoria-github-assets/vectorstaruml/spritely.8ed1b6d.uml)

#### Points of Interest
* Generic UML.*View XML Nodes now converted to generic UMLView Java objects.
* Since that's working, immediately next is exploring SVG file creation.

#### Notes
* I'm still mostly messing around until I think through a proper model for all this.  Somewhere, I'll put a disclaimer or something stating no personal involvement with the wondrous work that is [StarUML](http://sourceforge.net/projects/staruml/).  All I'm doing here is translating their default-save XML file into a similarly-appearing SVG.
