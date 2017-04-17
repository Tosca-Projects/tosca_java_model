package parser;

import static org.junit.Assert.*;

import org.junit.Test;

import builder.ToscaBuilder;

class TestToscaSyntax {

	@Test
	public void test() {
		def s = new ToscaSyntax("service_template")
		s.root_entry.a("map").with {
			entry("tosca_definitions_version").mandatory()
			entry("metadata").a("map").with {
				entry "template_name"
				entry "template_version"
				entry "template_author"
			}
			entry "template_name"
			entry "template_version"
			entry "template_author"
			entry "description"
			entry("dsl_definitions").a("map")
			entry("repositories").a("map").with {
				any_keyword().with {
					entry "description"
					entry "url"
					entry("credential").a("map").with {
						entry "protocol"
						entry("token_type").mandatory()
						entry("token").mandatory()
						entry("keys").a("map")
						entry "user"
					}
				}
			}
			entry("topology_template").a("map").with {
				entry "description"
				entry("node_templates").a("map").with {
					any_keyword().with {
						entry("type").mandatory()
						entry "description"
						entry("metadata").a("map").with {
							entry "template_name"
							entry "template_version"
							entry "template_author"
						}
						entry("directives").a("list")
						entry("properties").a("map")
					}
				}
			}
		}
		assert s.check(ToscaBuilder.simple_service_template()).OK
		assert s.check(ToscaBuilder.simple_service_template2()).OK
		assert s.check(ToscaBuilder.wrong_service_template()).OK == false
		assert s.check(ToscaBuilder.wrong_service_template2()).OK == false
		assert s.check(ToscaBuilder.simple_topology_template()).OK
	}
}
