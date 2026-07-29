# MotionLayout

[MotionLayout](MotionLayout.kt) which updates the position of all children at the conclusion of a fixed interval.
All children must implement the [MovingView](../movingview) interface, and the updatePosition method is called on all children at the conclusion of each interval.

MotionLayout extends the ViewGroup class and inherits all values, methods, and attributes from that class.
See the ViewGroup documentation for more information about this class: <https://developer.android.com/reference/android/view/ViewGroup>.

## XML Attributes

### Summary

| XML Attribute                            | Summary                                             | Type    | Required | Default |
|:-----------------------------------------|:----------------------------------------------------|:--------|:---------|:--------|
| [app:motionInterval](#appmotioninterval) | Amount of time, in seconds, to wait between updates | integer | No       | 0       |
| [app:paused](#apppaused)                 | If view motion is paused                            | boolean | No       | false   |


### app:motionInterval

Integer value indicating the time between position updates, in seconds.
This value is assigned to the motionInterval property.


### app:paused

Boolean value indicating if position updates are paused.
This value is assigned to the paused property.


## Methods

### forceUpdate

Force the child positions to update immediately, even if motion is paused.
Call updateChildren on all children.

This method takes the following parameters, and returns Unit:

| Parameter         | Summary                                                              | Type     | Default |
|:------------------|:---------------------------------------------------------------------|:---------|:--------|
| forceChildUpdates | : If the forceUpdate flag should be use when invoking updatePosition | Boolean  | false   |
