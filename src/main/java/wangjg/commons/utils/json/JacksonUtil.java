package wangjg.commons.utils.json;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * 
 * @author wangjg
 *
 */
@Deprecated
public class JacksonUtil extends BaseJsonUtil {
	protected ApiObjectMapper mapper;

	public JacksonUtil() {
		mapper = new ApiObjectMapper();
		mapper.setIgnoreNull(true);
	}

	public ApiObjectMapper getMapper() {
		return this.mapper;
	}

	public <T> List<T> jsonToList(String jsonStr, String path, Class<T> cls) throws IOException {
		List<T> list = this.jsonToObj(jsonStr, path, this.constructType(ArrayList.class, cls));
		return list;
	}

	public <T> T jsonToObj(String jsonStr, String path, Type type) throws IOException {
		JsonNode jsonNode = this.findNode(jsonStr, path);
		T bean = (T) this.nodeToBean(jsonNode, type);
		return bean;
	}

	public <T> T jsonToObj(String jsonStr, Class<T> cls) {
		if (jsonStr == null || cls == null) {
			return null;
		}
		try {
			return this.getMapper().readValue(jsonStr, cls);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public <T> List<T> jsonToList(String jsonVal, Class<T> clazz) {
		TypeFactory t = TypeFactory.defaultInstance();
		try {
			List<T> list = this.getMapper().readValue(jsonVal, t.constructCollectionType(ArrayList.class, clazz));
			return list;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Map<?, ?> jsonToMap(String jsonVal) {
		try {
			Map<?, ?> map = this.getMapper().readValue(jsonVal, Map.class);
			return map;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String objToJson(Object obj) {
		try {
			return this.getMapper().writeValueAsString(obj);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取Jackson的集合类型
	 *
	 * @param mapper
	 * @param collectionClass
	 *            集合类型
	 * @param elementClasses
	 *            集合元素类型
	 * @return
	 */
	protected JavaType constructType(Class<?> collectionClass, Class<?>... elementClasses) {
		return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
	}

	public String objToPrettyJson(Object obj) {
		try {
			String json = this.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(obj);
			return json;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Object nodeToBean(JsonNode jsonNode, Type type) throws IOException {
		Type rawType = null;
		if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
			rawType = pt.getRawType();
		} else {
			rawType = type;
		}

		if (jsonNode == null) {
			return null;
		}
		if (String.class.equals(type)) {
			return jsonNode.toString();
		}

		Object obj = mapper.readValue(mapper.treeAsTokens(jsonNode), mapper.constructType(type));
		return obj;

	}

	private JsonNode findNode(String json, String path) throws IOException {
		JsonNode root = mapper.readTree(json);
		return this.findNode(root, path);
	}

	private JsonNode findNode(JsonNode root, String path) {
		JsonNode dataNode = root;

		if (path != null && path.length() > 0) {
			String ss[] = path.split("\\.");
			for (int i = 0; i < ss.length; i++) {
				String s = ss[i];
				dataNode = dataNode.get(s);
			}
		}

		return dataNode;
	}

	public void setDateFormat(String dateFormat) {
		this.mapper.setDateFormat(dateFormat);
	}

	@Deprecated
	public void convertList(List list, Class beanClass) {
		for (int i = 0; i < list.size(); i++) {
			Object bean = list.get(i);
			String json = this.objToJson(bean);
			list.set(i, jsonToObj(json, beanClass));
		}
	}

}
