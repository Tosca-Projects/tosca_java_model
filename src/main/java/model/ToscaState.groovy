package model

import java.util.Map;

abstract class ToscaState {

	String name
	Map model

	ToscaState(String name, state_def, boolean typed=true) {
		if (!(state_def instanceof Map)) {
			throw new Exception("'$name' definition should be a map")
		}
		this.name = name
		this.model = state_def
		if (typed) {
			ToscaModel.checkRequired(model, ['type'])
		}
	}

	String getType() {
		return model.'type'
	}

	String getDescription() {
		return model.'description'
	}

	boolean getRequired() {
		if (model.'required' == null) {
			return true
		}
		def rs = model.'required'.toString()
		try {
			return Boolean.parseBoolean(rs)
		}
		catch(Exception e) {
			throw new Exception("'required' should have a boolean value for property '$name'")
		}
	}

	Object getDefault() {
		if (model.'default' == null) {
			return null
		}
		def os = model.'default'.toString()
		try {
			if (type == 'integer') {
				return Integer.parseInt(os)
			}
			if (type == 'float') {
				return Float.parseFloat(os)
			}
			if (type == 'boolean') {
				return Boolean.parseBoolean(os)
			}
		}
		catch(Exception e) {
			throw new Exception("default value '$os' is not consistent with type '$type' for '$name'")
		}
		return os
	}

	String getStatus() {
		if (model.'status' == null) {
			return 'supported'
		}
		def s = model.'status'
		def valid_status = ['supported', 'unsupported', 'experimental', 'deprecated']
		if (!(s instanceof String) || !(s in valid_status)) {
			throw new Exception("Invalid status '$s' in '$name'")
		}
	}

	EntrySchema getEntry_schema() {
		if (model.'entry_schema' == null) {
			return null
		}

		if ((model.'entry_schema' instanceof String)||(model.'entry_schema' instanceof Map)) {
			return new EntrySchema(model.'entry_schema')
		}

		throw new Exception("Invalid entry_schema definition")
	}
}
