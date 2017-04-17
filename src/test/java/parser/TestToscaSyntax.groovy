package parser;

import static org.junit.Assert.*;

import org.junit.Test;

import builder.ToscaBuilder;

class TestToscaSyntax {

	@Test
	public void test() {
		def s = new ToscaSyntax("service_template")
		s.root_entry.is_a("map").with {
			entry("tosca_definitions_version").is_mandatory()
			entry("metadata").is_a("map").with {
				entry "template_name"
				entry "template_version"
				entry "template_author"
			}
			entry "template_name"
			entry "template_version"
			entry "template_author"
			entry "description"
			entry("dsl_definitions").is_a("map")
			entry("repositories").is_a("map").with {
				any_keyword().with {
					entry "description"
					entry "url"
					entry("credential").is_a("map").with {
						entry "protocol"
						entry("token_type").is_mandatory()
						entry("token").is_mandatory()
						entry("keys").is_a("map")
						entry "user"
					}
				}
			}
		}
		assert s.check(ToscaBuilder.simple_service_template()).OK
		assert s.check(ToscaBuilder.wrong_service_template()).OK == false
	}
}
