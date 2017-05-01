package parser

class ToscaSyntax {

	String root
	ToscaKeyword root_entry

	ToscaSyntax(String root) {
		this.root = root
		root_entry = new ToscaKeyword(root)
	}

	ValidationResult check(model) {
		def vr = new ValidationResult()
		root_entry.check(model, vr)
		return vr
	}
	
	static ToscaSyntax getServiceTemplateSyntax(String version) {
		if (version != "1.1") {
			throw new Exception("only version 1.1 is supported at this time")
		}
		def s = new ToscaSyntax("service_template")
		s.root_entry.a("map").with {
			string_entry("tosca_definitions_version").mandatory()
			metadata_entry()
			string_entry "template_name"
			string_entry "template_version"
			string_entry "template_author"
			string_entry "description"
			map_entry("dsl_definitions").with {
				any_map_entry().with {
					any_entry()
				}
			}
			map_entry("repositories").with {
				any_map_entry().with {
					string_entry("description")
					string_entry("url")
					map_entry("credential").with {
						string_entry("protocol")
						string_entry("token_type").mandatory()
						string_entry("token").mandatory()
						map_entry("keys")
						string_entry("user")
					}
				}
			}
			list_entry("imports").with {
				any_map_entry().with {
					string_entry("file").mandatory()
					string_entry("repository")
					string_entry("namespace_uri")
					string_entry("namespace_prefix")
					or_a("string")
				}
			}
			map_entry("artifact_types").with {
				any_map_entry().with {
					string_entry "derived_from"
					string_entry "version"
					metadata_entry()
					string_entry "description"
					string_entry "mime_type"
					list_entry("file_ext")
					property_definitions_entry()
				}
			}
			map_entry("topology_template").with {
				string_entry "description"
				map_entry("inputs").with {
					any_map_entry().with {
						string_entry "type"
						string_entry "description"
						boolean_entry "required"
						entry "default"
						string_entry "status"
						constraints_entry()
					}
				}
				map_entry("node_templates").with {
					any_map_entry().with {
						string_entry("type").mandatory()
						string_entry "description"
						metadata_entry()
						list_entry("directives").with { any_string_entry() }
						property_assignments_entry()
						capabilities_entry()
						requirements_entry()					
					}
				}
				map_entry("outputs").with {
					any_map_entry().with {
						string_entry "description"
						entry "value"
					}
				}
			}
		}
		return s
	}
	
}


