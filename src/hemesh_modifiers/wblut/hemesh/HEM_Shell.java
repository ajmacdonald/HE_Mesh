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

import gnu.trove.iterator.TLongLongIterator;
import gnu.trove.map.TLongLongMap;
import wblut.math.WB_ConstantScalarParameter;
import wblut.math.WB_FactorScalarParameter;
import wblut.math.WB_ScalarParameter;

/**
 * Turns a solid into a rudimentary shelled structure.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEM_Shell extends HEM_Modifier {
	/**
	 *
	 */
	private WB_ScalarParameter d;

	private boolean useFace;

	/**
	 *
	 */
	public HEM_Shell() {
		super();
		d = WB_ScalarParameter.ZERO;
		useFace = false;
	}

	/**
	 *
	 *
	 * @param d
	 * @return
	 */
	public HEM_Shell setThickness(final double d) {
		this.d = d == 0.0 ? WB_ScalarParameter.ZERO : new WB_ConstantScalarParameter(d);
		return this;
	}

	/**
	 *
	 *
	 * @param d
	 * @return
	 */
	public HEM_Shell setThickness(final WB_ScalarParameter d) {
		this.d = d;
		return this;
	}

	/**
	 *
	 * @param b
	 * @return
	 */
	public HEM_Shell setUseFaceExpand(final boolean b) {
		useFace = b;
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	protected HE_Mesh applySelf(final HE_Mesh mesh) {
		if (d == WB_ScalarParameter.ZERO) {
			return mesh;
		}

		HEC_Copy cc = new HEC_Copy().setMesh(mesh);
		final HE_Mesh innerMesh = cc.create();
		TLongLongMap heCorrelation = cc.halfedgeCorrelation;
		if (!useFace) {
			final HEM_VertexExpand expm = new HEM_VertexExpand().setDistance(new WB_FactorScalarParameter(-1.0, d));
			innerMesh.modify(expm);
		} else {
			final HEM_FaceExpand expm = new HEM_FaceExpand().setDistance(new WB_FactorScalarParameter(-1.0, d));
			innerMesh.modify(expm);
		}

		HET_MeshOp.flipFaces(innerMesh);
		mesh.add(innerMesh);
		HE_Halfedge he1, he2, heio, heoi;
		HE_Face fNew;
		for (TLongLongIterator it = heCorrelation.iterator(); it.hasNext();) {
			it.advance();
			he1 = mesh.getHalfedgeWithKey(it.key());
			if (he1.isOuterBoundary()) {
				he2 = mesh.getHalfedgeWithKey(it.value());
				heio = new HE_Halfedge();
				heoi = new HE_Halfedge();
				mesh.setVertex(heio, he1.getPair().getVertex());
				heio.setUVW(he1.getPair().getVertex().getUVW(he1.getPair().getFace()));
				mesh.setVertex(heoi, he2.getPair().getVertex());
				heoi.setUVW(he2.getPair().getVertex().getUVW(he2.getPair().getFace()));
				mesh.setNext(he1, heio);
				mesh.setNext(heio, he2);
				mesh.setNext(he2, heoi);
				mesh.setNext(heoi, he1);
				fNew = new HE_Face();
				fNew.setInternalLabel(1);
				mesh.add(fNew);
				mesh.setHalfedge(fNew, he1);
				mesh.setFace(he1, fNew);
				mesh.setFace(he2, fNew);
				mesh.setFace(heio, fNew);
				mesh.setFace(heoi, fNew);
				mesh.add(heio);
				mesh.add(heoi);
			}

		}
		mesh.pairHalfedges();
		mesh.capHalfedges();
		return mesh;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	protected HE_Mesh applySelf(final HE_Selection selection) {
		return applySelf(selection.parent);
	}
}
