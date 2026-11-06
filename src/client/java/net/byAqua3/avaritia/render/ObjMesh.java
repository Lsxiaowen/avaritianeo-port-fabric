package net.byAqua3.avaritia.render;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public record ObjMesh(List<Vertex> vertices) {
	public ObjMesh {
		vertices = List.copyOf(vertices);
	}

	public record Vertex(float x, float y, float z, float u, float v, float nx, float ny, float nz) {
	}

	public static ObjMesh read(Reader input) throws IOException {
		List<float[]> positions = new ArrayList<>();
		List<float[]> uvs = new ArrayList<>();
		List<float[]> normals = new ArrayList<>();
		List<Vertex> output = new ArrayList<>();
		BufferedReader reader = input instanceof BufferedReader buffered ? buffered : new BufferedReader(input);
		String line;
		while ((line = reader.readLine()) != null) {
			line = line.split("#", 2)[0].trim();
			if (line.isEmpty()) {
				continue;
			}
			String[] fields = line.split("\\s+");
			switch (fields[0]) {
				case "v" -> positions.add(new float[] { Float.parseFloat(fields[1]), Float.parseFloat(fields[2]), Float.parseFloat(fields[3]) });
				case "vt" -> uvs.add(new float[] { Float.parseFloat(fields[1]), Float.parseFloat(fields[2]) });
				case "vn" -> normals.add(new float[] { Float.parseFloat(fields[1]), Float.parseFloat(fields[2]), Float.parseFloat(fields[3]) });
				case "f" -> {
					List<Vertex> face = new ArrayList<>();
					for (int index = 1; index < fields.length; index++) {
						String[] indices = fields[index].split("/", -1);
						float[] position = positions.get(index(indices[0], positions.size()));
						float[] uv = indices.length > 1 && !indices[1].isEmpty() ? uvs.get(index(indices[1], uvs.size())) : new float[2];
						float[] normal = indices.length > 2 && !indices[2].isEmpty() ? normals.get(index(indices[2], normals.size())) : new float[] { 0, 1, 0 };
						face.add(new Vertex(position[0], position[1], position[2], uv[0], uv[1], normal[0], normal[1], normal[2]));
					}
					if (face.size() == 4) {
						output.addAll(face);
					} else {
						for (int index = 1; index + 1 < face.size(); index++) {
							output.add(face.getFirst());
							output.add(face.get(index));
							output.add(face.get(index + 1));
							output.add(face.get(index + 1));
						}
					}
				}
				default -> { }
			}
		}
		return new ObjMesh(output);
	}

	private static int index(String value, int count) {
		int index = Integer.parseInt(value);
		return index < 0 ? count + index : index - 1;
	}

	public void render(PoseStack.Pose pose, VertexConsumer buffer, int color, int light) {
		for (Vertex vertex : vertices) {
			buffer.addVertex(pose, vertex.x(), vertex.y(), vertex.z()).setColor(color).setUv(vertex.u(), vertex.v())
					.setOverlay(0).setLight(light).setNormal(pose, vertex.nx(), vertex.ny(), vertex.nz());
		}
	}
}
