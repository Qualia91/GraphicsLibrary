# **Graphics Library**
## Graphics library using lwjgl3 and Java 14

Graphics library written from the ground up as a way of learning game engine development.

### Getting Started

Create a window like so:

```
try (Window window = new Window(
			1200,
			800,
			"Window Title")) {
      
  window.init();
  
  while (!window.shouldClose()) {
  
    window.loop(gameObjects, hudObjects, cameraUUID);  
    
  }
  
} catch (Exception e) {
  e.printStackTrace();
}
```

Passing it width, height and a window title. It impliments Autoclosable and so when used in a try with resources statement, it will properly close the window when finished.

The window will close if esc key is pressed, or x is pressed.

#### Game Objects
Game objects are a hash map of SceneGraph objects and the SceneGraph UUID contained within.

## Examples

The Testbench.java file in tests contains lots of examples, look there for more help.
