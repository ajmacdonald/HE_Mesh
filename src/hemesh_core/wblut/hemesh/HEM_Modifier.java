/*
 * This file is part of HE_Mesh, a library for creating and manipulating meshes.
 * It is dedicated to the public domain. To the extent possible under law,
 * I , Frederik Vanhoutte, have waived all copyright and related or neighboring
 * rights.
 *
 * This work is published from Belgium. (http://creativecommons.org/publicdomain/zero/1.0/)
 *
 */
package wblut.hemesh;

/**
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
abstract public class HEM_Modifier extends HE_Machine {
	/**
	 * Instantiates a new HEM_Modifier.
	 */
	public HEM_Modifier() {
	}

	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		tracker.setStartStatus(this, "Starting modifier.");
		if (mesh == null || mesh.getNumberOfVertices() == 0) {
			return new HE_Mesh();
		}
		HE_Mesh copy = mesh.get();
		try {
			HE_Mesh result = applySelf(mesh);
			tracker.setStopStatus(this, "Mesh modified.");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			mesh.setNoCopy(copy);
			tracker.setStopStatus(this, "Modifier failed. Resetting mesh.");
			return mesh;
		}

	}

	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		tracker.setStartStatus(this, "Starting modifier.");
		if (selection == null) {
			return new HE_Mesh();
		}
		HE_Mesh copy = selection.parent.get();
		try {
			HE_Mesh result = applySelf(selection);
			tracker.setStopStatus(this, "Mesh modified.");

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			selection.parent.setNoCopy(copy);
			tracker.setStopStatus(this, "Modifier failed. Resetting mesh.");
			return selection.parent;
		}

	}

	protected abstract HE_Mesh applySelf(final HE_Mesh mesh);

	protected abstract HE_Mesh applySelf(final HE_Selection selection);

}
