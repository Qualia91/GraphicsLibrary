module com.nick.wood.graphics_library {
	requires com.nick.wood.maths;
	requires org.lwjgl.opengl;
	requires org.lwjgl.stb;
	requires org.lwjgl.glfw;
	requires org.lwjgl.opengles;
	requires org.lwjgl.assimp;
	requires org.lwjgl.opengl.natives;
	requires org.lwjgl.glfw.natives;
	requires org.lwjgl.natives;
	exports com.nick.wood.graphics_library_3d;
	exports com.nick.wood.graphics_library_3d.objects;
	exports com.nick.wood.graphics_library_3d.objects.scene_graph_objects;
	exports com.nick.wood.graphics_library_3d.input;
	exports com.nick.wood.graphics_library_3d.lighting;
	exports com.nick.wood.graphics_library_3d.objects.mesh_objects;
}