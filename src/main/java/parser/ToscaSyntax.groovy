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
	
	ToscaSyntax getServiceTemplateSyntax(String version) {
		if (version != "1.1") {
			throw new Exception("only version 1.1 is supported at this time")
		}
		def s = new ToscaSyntax("service_template")
		s.root_entry.a("map").with {
			a_string("tosca_definitions_version").mandatory()
			a_map("metadata").with {
				a_string "template_name"
				a_string "template_version"
				a_string "template_author"
			}
			a_string "template_name"
			a_string "template_version"
			a_string "template_author"
			a_string "description"
			a_map("dsl_definitions").with {
				any_entry().with {
					any_entry()
				}
			}
			a_map("repositories").with {
				any_entry().with {
					a_string("description")
					a_string("url")
					a_map("credential").with {
						a_string("protocol")
						a_string("token_type").mandatory()
						a_string("token").mandatory()
						a_map("keys")
						a_string("user")
					}
				}
			}
			a_list("imports").with {
				any_map().with {
					a_string("file").mandatory()
					a_string("repository")
					a_string("namespace_uri")
					a_string("namespace_prefix")
					or_a("string")
				}
			}
			a_map("artifact_types").with {
				any_map().with {
					a_string "derived_from"
					a_string "version"
					a_map("metadata").with {
						a_string "template_name"
						a_string "template_version"
						a_string "template_author"
					}
					a_string "description"
					a_string "mime_type"
					a_list("file_ext")
					a_properties_entry()
				}
			}
			a_map("topology_template").with {
				a_string "description"
				a_map("inputs").with {
					any_map().with {
						a_string "type"
						a_string "description"
						a_boolean "required"
						entry "default"
						a_string "status"
						a_constraints_entry()
					}
				}
				a_map("node_templates").with {
					any_map().with {
						a_string("type").mandatory()
						a_string "description"
						a_map("metadata").with {
							a_string "template_name"
							a_string "template_version"
							a_string "template_author"
						}
						a_list "directives"
						a_properties_entry()
						a_map("capabilities").with {
							any_map().with {
								a_map("properties").with { any_entry() }
								a_map("attributes").with { any_entry() }
							}
						}
					}
				}
			}
		}
		return s
	}
	
}


