# SingleChildLayout

SingleChildLayout is an interface for layouts whose children use a single layout file.
Using a resource file configured in the [XML attributes](#xml-attributes), the layout generates its children on initialization.

## XML Attributes

### Summary

| XML Attribute                      | Summary                             | Type      | Required |
|:-----------------------------------|:------------------------------------|:----------|:---------|
| [app:childLayout](#appchildLayout) | Layout resource to use for children | reference | Yes      |
| [app:numChildren](#appnumChildren) | Number of children to generate      | int       | Yes      |

### app:childLayout

Reference to layout resource for all children.

### app:numChildren

Number of children to generate in layout.
After layout has been initialized, this will be equal to the childCount property.

## Child Management

A SingleChildLayout cannot be initialized with children.
All children will be generated automatically and added to the layout based on the provided XML attributes.

Children cannot be added to or removed from a SingleChildLayout after initialization.
