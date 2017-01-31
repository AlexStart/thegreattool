/**
 * 
 */
package com.sam.jcc.cloud.adapter;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.sam.jcc.cloud.utils.files.FileManager;

import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataProvider;

/**
 * @author olegk
 *
 */
//TODO(a bad part of the app): works with static data, needs manual update
public class StaticMetadataProvider implements InitializrMetadataProvider {

	@Override
	public InitializrMetadata get() {
		return new Gson().fromJson(readMetadata(), InitializrMetadata.class);
	}

	private String readMetadata() {
		return new String(FileManager.getResourceAsBytes(getClass(), "/metadata.json"), Charsets.UTF_8);
	}
}