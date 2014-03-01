package de.galan.verjson.transformation;

import com.google.gson.JsonElement;


/**
 * Empty version that does no operations in the transformations.
 * 
 * @author daniel
 */
public class EmptyVersion extends AbstractVersion {

	private long targetVersion;


	public EmptyVersion(long targetVersion) {
		this.targetVersion = targetVersion;
	}


	@Override
	public void transform(JsonElement element) {
		// noop
	}


	@Override
	public long getTargetVersion() {
		return targetVersion;
	}

}
