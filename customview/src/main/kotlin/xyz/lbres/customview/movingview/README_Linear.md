# Linear Motion

Linear motion means that position updates are made along a straight line.
When the view reaches the edge of its parent, a new line is selected, and movement continues along that line.

The [LinearMovingView](LinearMovingView.kt) interface extends the MovingView interface with additional functionality that is unique to linear motion.

## XML Attributes

### Summary

| XML Attribute                        | Summary                      | Type    | Required | Default |
|:-------------------------------------|:-----------------------------|:--------|:---------|:--------|
| [app:movementSize](#appmovementsize) | Size of each position update | integer | No       | 0       |

### app:movementSize

Integer value indicating the size of each position update.
This value is assigned to the movementSize property.


## Implementations

### LinearMovingTextView

[LinearMovingTextView](LinearMovingTextView.kt) extends the TextView class and implements the LinearMovingView interface. It inherits all values, methods, and attributes from the class and interface.

See the TextView documentation for more information about this class: https://developer.android.com/reference/android/widget/TextView.
