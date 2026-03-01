# CircleLayout

CircleLayout is a custom layout implementation to position children along the circumference of a circle.
The positions of children are calculated using the radius, the angle separating children, and the start angle for the circle.
These values can be customized using [XML attributes](#xml-attributes).

CircleLayout extends the ViewGroup class.

## XML Attributes

### Summary

| XML Attribute                              | Summary                                                                      | Type      | Required |
|:-------------------------------------------|:-----------------------------------------------------------------------------|:----------|:---------|
| [app:angleMode](#appanglemode)             | Mode to calculate the spacing between children                               | enum      | No       |
| [app:radiusMode](#appradiusmode)           | Mode to calculate the radius of the circle                                   | enum      | No       |
| [app:radiusPercent](#appradiuspercent)     | Percentage of layout size that is used to calculate the radius of the circle | float     | No       |
| [app:radiusSize](#appradiussize)           | Fixed size for radius                                                        | dimension | No       |
| [app:separationAngle](#appseparationangle) | Fixed angle between children                                                 | int       | No       |
| [app:startAngle](#appstartangle)           | Angle where first child is positioned                                        | int       | No       |

### app:angleMode

The mode to determine how the spacing between children is calculated.
There are two possible values, which correspond to values in the AngleMode enum class.

| Value       | AngleMode Value       | Requirements                                           |
|:------------|:----------------------|:-------------------------------------------------------|
| distributed | AngleMode.DISTRIBUTED |                                                        |
| fixed       | AngleMode.FIXED       | [app:separationAngle](#appseparationangle) must be set |

Spacing is calculated using AngleMode.DISTRIBUTED if no value is specified, or if the requirements for the other mode are not met.

### app:radiusMode

The mode to determine how the radius of the circle is calculated.
There are three possible values, which correspond to values in the RadiusMode enum class.

| Value       | RadiusMode Value        | Requirements                                       |
|:------------|:------------------------|:---------------------------------------------------|
| fitChildren | RadiusMode.FIT_CHILDREN |                                                    |
| fixed       | RadiusMode.FIXED        | [app:radiusSize](#appradiussize) must be set       |
| percent     | RadiusMode.PERCENT      | [app:radiusPercent](#appradiuspercent) must be set |

Radius is calculated using RadiusMode.FIT_CHILDREN if no value is specified, or if the requirements for the other modes are not met.

### app:radiusPercent

Percentage of layout size that is used to calculate the radius size.
Radius size is calculated using the minimum dimension of the layout.
Used only if [app:radiusMode](#appradiusmode) is set to percent.

### app:radiusSize

Fixed size for the radius of the circle. Does not consider layout size or children.
Used only if [app:radiusMode](#appradiusmode) is set to fixed.

### app:separationAngle

Fixed number of degrees between children. Does not consider circumference or number of children.
Used only if [app:angleMode](#appanglemode) is set to fixed.

### app:startAngle

Offset of the first child, in degrees. If no value is specified, the first child is positioned at the top of the circle (0 degrees).

## Inherited Values and Methods

CircleLayout inherits all values, methods, and attributes from the ViewGroup class.
See the [ViewGroup documentation](https://developer.android.com/reference/android/view/ViewGroup) for details.

# SingleChildCircleLayout

SingleChildCircleLayout extends the CircleLayout class and implements the SingleChildLayout interface.
It inherits all values, methods, and attributes from the class and interface.

See [here](../interfaces/singlechildlayout/README.md) for more information about SingleChildLayout.
