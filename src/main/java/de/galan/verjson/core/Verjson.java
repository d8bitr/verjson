package de.galan.verjson.core;

import java.util.Map;

import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;

import de.galan.commons.logging.Logr;
import de.galan.verjson.transformation.Step;


/**
 * TODO documentation
 *
 * @author daniel
 * @param <T> ...
 */
public class Verjson<T> {

	private static final Logger LOG = Logr.get();

	/** Optional user-defined namespace to distinguish between different types */
	String namespace;

	/** Type of the serialized objects */
	private Class<T> valueClass;

	private Map<Long, Step> steps;

	private ObjectMapper mapper;

	/** Highest version available in added transformers, starting with 1. */
	long largestTargetVersion = 1L;


	public static <T> Verjson<T> create(Class<T> valueClass, Versions versions) {
		return new Verjson<T>(valueClass, versions);
	}


	public Verjson(Class<T> valueClass, Versions versions) {
		this.valueClass = Preconditions.checkNotNull(valueClass, "valueClass can not be null");
		Versions vs = (versions != null) ? versions : new Versions();
		configure(vs);
	}


	protected void configure(Versions versions) {
		versions.configure();
		this.namespace = versions.getNamespace();
		mapper = new ObjectMapperFactory().create(versions);

		/*
		parser = new JsonParser();
		containers = Maps.newTreeMap();

		for (Version version: versions.getVersions()) {
			appendVersion(version);
		}
		for (Entry<Type, Object> entry: versions.getTypeAdapter().entrySet()) {
			builder.registerTypeAdapter(entry.getKey(), entry.getValue());
		}

		gson = builder.create();
		fillVersionGaps();
		 */
	}


	protected String getNamespace() {
		return namespace;
	}


	//TODO own exception
	public String write(T obj) throws JsonProcessingException {
		MetaWrapper wrapper = new MetaWrapper(getHighestTargetVersion(), getNamespace(), obj);
		return mapper.writeValueAsString(wrapper);
	}


	public T read(String json) throws VersionNotSupportedException, NamespaceMismatchException {
		//mapper.readvalue
		return null;
		//read(parse(json));
	}


	protected long getHighestTargetVersion() {
		return largestTargetVersion;
	}

}
