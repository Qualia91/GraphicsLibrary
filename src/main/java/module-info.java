module com.boc_dev.graphics_library {
	requires com.boc_dev.maths;
	requires org.lwjgl.opengl;
	requires org.lwjgl.stb;
	requires org.lwjgl.glfw;
	requires org.lwjgl.opengles;
	requires org.lwjgl.assimp;
	requires org.lwjgl.opengl.natives;
	requires org.lwjgl.glfw.natives;
	requires org.lwjgl.natives;
	requires com.boc_dev.event_bus;
	exports com.boc_dev.graphics_library.objects;
	exports com.boc_dev.graphics_library.objects.lighting;
	exports com.boc_dev.graphics_library.objects.mesh_objects;
	exports com.boc_dev.graphics_library;
	exports com.boc_dev.graphics_library.utils;
	exports com.boc_dev.graphics_library.objects.render_scene;
	exports com.boc_dev.graphics_library.input;
	exports com.boc_dev.graphics_library.frame_buffers;
	exports com.boc_dev.graphics_library.objects.materials;
	exports com.boc_dev.graphics_library.communication;
}