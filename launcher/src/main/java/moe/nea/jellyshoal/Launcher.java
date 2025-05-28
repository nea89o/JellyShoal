package moe.nea.jellyshoal;

public class Launcher {
	public static void main(String[] args) throws Exception {
		var method = Class.forName("moe.nea.jellyshoal.MainKt")
			.getDeclaredMethod("main", String[].class);
		method.invoke(null, new Object[]{args});
	}
}
