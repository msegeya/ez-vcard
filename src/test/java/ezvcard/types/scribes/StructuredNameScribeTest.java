package ezvcard.types.scribes;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import ezvcard.VCardDataType;
import ezvcard.types.StructuredNameType;
import ezvcard.types.scribes.StructuredNameScribe;
import ezvcard.types.scribes.Sensei.Check;
import ezvcard.util.JCardValue;

/*
 Copyright (c) 2013, Michael Angstadt
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met: 

 1. Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer. 
 2. Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution. 

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 The views and conclusions contained in the software and documentation are those
 of the authors and should not be interpreted as representing official policies, 
 either expressed or implied, of the FreeBSD Project.
 */

/**
 * @author Michael Angstadt
 */
public class StructuredNameScribeTest {
	private final StructuredNameScribe scribe = new StructuredNameScribe();
	private final Sensei<StructuredNameType> sensei = new Sensei<StructuredNameType>(scribe);

	private final StructuredNameType withAllValues = new StructuredNameType();
	{
		withAllValues.setGiven("Jonathan");
		withAllValues.setFamily("Doe");
		withAllValues.addAdditional("Joh;nny,");
		withAllValues.addAdditional("John");
		withAllValues.addPrefix("Mr.");
		withAllValues.addSuffix("III");
	}
	private final StructuredNameType withEmptyValues = new StructuredNameType();
	{
		withEmptyValues.setGiven("Jonathan");
		withEmptyValues.setFamily(null);
		withEmptyValues.addAdditional("Joh;nny,");
		withEmptyValues.addAdditional("John");
	}
	private final StructuredNameType empty = new StructuredNameType();

	@Test
	public void writeText() {
		sensei.assertWriteText(withAllValues).run("Doe;Jonathan;Joh\\;nny\\,,John;Mr.;III");
		sensei.assertWriteText(withEmptyValues).run(";Jonathan;Joh\\;nny\\,,John;;");
		sensei.assertWriteText(empty).run(";;;;");
	}

	@Test
	public void writeXml() {
		//@formatter:off
		sensei.assertWriteXml(withAllValues).run(
		"<surname>Doe</surname>" +
		"<given>Jonathan</given>" +
		"<additional>Joh;nny,</additional>" +
		"<additional>John</additional>" +
		"<prefix>Mr.</prefix>" +
		"<suffix>III</suffix>"
		);

		sensei.assertWriteXml(withEmptyValues).run(
		"<surname/>" +
		"<given>Jonathan</given>" +
		"<additional>Joh;nny,</additional>" +
		"<additional>John</additional>" +
		"<prefix/>" +
		"<suffix/>"
		);

		sensei.assertWriteXml(empty).run(
		"<surname/>" +
		"<given/>" +
		"<additional/>" +
		"<prefix/>" +
		"<suffix/>"
		);
		//@formatter:on
	}

	@Test
	public void writeJson() {
		sensei.assertWriteJson(withAllValues).run(JCardValue.structured(null, "Doe", "Jonathan", Arrays.asList("Joh;nny,", "John"), "Mr.", "III"));
		sensei.assertWriteJson(withEmptyValues).run(JCardValue.structured(null, "", "Jonathan", Arrays.asList("Joh;nny,", "John"), "", ""));
		sensei.assertWriteJson(empty).run(JCardValue.structured(null, "", "", "", "", ""));
	}

	@Test
	public void parseText() {
		sensei.assertParseText("Doe;Jonathan;Joh\\;nny\\,,John;Mr.;III").run(is(withAllValues));
		sensei.assertParseText(";Jonathan;Joh\\;nny\\,,John;;").run(is(withEmptyValues));
		sensei.assertParseText(";;;;").run(is(empty));
		sensei.assertParseText("").run(is(empty));
	}

	@Test
	public void parseXml() {
		//@formatter:off
		sensei.assertParseXml(
		"<surname>Doe</surname>" +
		"<given>Jonathan</given>" +
		"<additional>Joh;nny,</additional>" +
		"<additional>John</additional>" +
		"<prefix>Mr.</prefix>" +
		"<suffix>III</suffix>"
		).run(is(withAllValues));

		sensei.assertParseXml(
		"<surname/>" +
		"<given>Jonathan</given>" +
		"<additional>Joh;nny,</additional>" +
		"<additional>John</additional>" +
		"<prefix/>" +
		"<suffix/>"
		).run(is(withEmptyValues));

		sensei.assertParseXml(
		"<surname/>" +
		"<given/>" +
		"<additional/>" +
		"<prefix/>" +
		"<suffix/>"
		).run(is(empty));
		//@formatter:on
	}

	@Test
	public void parseHtml() {
		//@formatter:off
		sensei.assertParseHtml(
		"<div>" +
			"<span class=\"family-name\">Doe</span>" +
			"<span class=\"given-name\">Jonathan</span>" +
			"<span class=\"additional-name\">Joh;nny,</span>" +
			"<span class=\"additional-name\">John</span>" +
			"<span class=\"honorific-prefix\">Mr.</span>" +
			"<span class=\"honorific-suffix\">III</span>" +
		"</div>"
		).run(is(withAllValues));

		sensei.assertParseHtml(
		"<div>" +
			"<span class=\"given-name\">Jonathan</span>" +
			"<span class=\"additional-name\">Joh;nny,</span>" +
			"<span class=\"additional-name\">John</span>" +
		"</div>"
		).run(is(withEmptyValues));
		
		sensei.assertParseHtml(
		"<div>" +
			"<span class=\"given-name\"></span>" +
		"</div>"
		).run(is(empty));
		//@formatter:on
	}

	@Test
	public void parseJson() {
		JCardValue value = JCardValue.structured(VCardDataType.TEXT, "Doe", "Jonathan", Arrays.asList("Joh;nny,", "John"), "Mr.", "III");
		sensei.assertParseJson(value).run(is(withAllValues));

		value = JCardValue.structured(VCardDataType.TEXT, null, "Jonathan", Arrays.asList("Joh;nny,", "John"), "", null);
		sensei.assertParseJson(value).run(is(withEmptyValues));

		value = JCardValue.structured(VCardDataType.TEXT, null, "Jonathan", Arrays.asList("Joh;nny,", "John"));
		sensei.assertParseJson(value).run(is(withEmptyValues));

		value = JCardValue.structured(VCardDataType.TEXT, null, null, "", null, null);
		sensei.assertParseJson(value).run(is(empty));

		sensei.assertParseJson("").run(is(empty));
	}

	private Check<StructuredNameType> is(final StructuredNameType expected) {
		return new Check<StructuredNameType>() {
			public void check(StructuredNameType actual) {
				assertEquals(expected.getFamily(), actual.getFamily());
				assertEquals(expected.getGiven(), actual.getGiven());
				assertEquals(expected.getAdditional(), actual.getAdditional());
				assertEquals(expected.getPrefixes(), actual.getPrefixes());
				assertEquals(expected.getSuffixes(), actual.getSuffixes());
			}
		};
	}
}