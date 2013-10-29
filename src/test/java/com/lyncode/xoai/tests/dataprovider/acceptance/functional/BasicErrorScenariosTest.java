package com.lyncode.xoai.tests.dataprovider.acceptance.functional;

import com.lyncode.xoai.builders.DateBuilder;
import com.lyncode.xoai.builders.ListBuilder;
import com.lyncode.xoai.builders.MapBuilder;
import com.lyncode.xoai.builders.OAIRequestParametersBuilder;
import com.lyncode.xoai.dataprovider.OAIRequestParameters;
import com.lyncode.xoai.dataprovider.core.Granularity;
import com.lyncode.xoai.dataprovider.exceptions.ConfigurationException;
import com.lyncode.xoai.dataprovider.exceptions.InvalidContextException;
import com.lyncode.xoai.dataprovider.exceptions.OAIException;
import com.lyncode.xoai.dataprovider.exceptions.WritingXmlException;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.lyncode.xoai.dataprovider.core.Granularity.Second;
import static com.lyncode.xoai.tests.SyntacticSugar.and;
import static com.lyncode.xoai.tests.SyntacticSugar.given;
import static org.hamcrest.MatcherAssert.assertThat;

public class BasicErrorScenariosTest extends AbstractDataProviderTest {

    private static final Date DATE_IN_PAST = new DateBuilder().subtractDays(1).build();
    private static final Date NOW = new Date();

    @Test
    public void shouldNotAllowTwoVerbs () throws WritingXmlException, OAIException, InvalidContextException, IOException, XMLStreamException, ConfigurationException {
        afterHandling(aRequest().with("verb", "one", and("two")));

        assertThat(theResult(), hasXPath("//o:error/@code", "badVerb"));
    }

    @Test
    public void shouldNotAllowWrongVerbs () throws WritingXmlException, OAIException, InvalidContextException, IOException, XMLStreamException, ConfigurationException {
        afterHandling(aRequest().withVerb("wrong"));

        assertThat(theResult(), hasXPath("//o:error/@code", "badVerb"));
    }

    @Test
    public void shouldNotAllowNoVerbs () throws WritingXmlException, OAIException, InvalidContextException, IOException, XMLStreamException, ConfigurationException {
        afterHandling(aRequest().withVerb(missing()));

        assertThat(theResult(), hasXPath("//o:error/@code", "badVerb"));
    }

    @Test
    public void shouldNotAllowMalformedFromDate () throws WritingXmlException, OAIException, InvalidContextException, IOException, XMLStreamException, ConfigurationException {
        afterHandling(aRequest().withVerb("ListRecords").with("from", "unExpectedFormat"));

        assertThat(theResult(), hasXPath("//o:error/@code", "badArgument"));
        assertThat(theResult(), hasXPath("//o:error", "The from parameter given is not valid"));
    }

    @Test
    public void shouldNotAllowMalformedUntilDate () throws WritingXmlException, OAIException, InvalidContextException, IOException, XMLStreamException, ConfigurationException {
        afterHandling(aRequest().withVerb("ListRecords").with("until", "unExpectedFormat"));

        assertThat(theResult(), hasXPath("//o:error/@code", "badArgument"));
        assertThat(theResult(), hasXPath("//o:error", "The until parameter given is not valid"));
    }

    @Test
    public void shouldNotAllowUntilDatesBeforeFromDates () throws WritingXmlException, OAIException, InvalidContextException, IOException, XMLStreamException, ConfigurationException {
        given(theRepositoryIsConfiguredto())
                .resolveTheGranularityTo(Second);

        afterHandling(aRequest().withVerb("ListRecords")
                .withMetadataPrefix("xoai")
                .withFrom(NOW)
                .withUntil(DATE_IN_PAST));

        assertThat(theResult(), hasXPath("//o:error/@code", "badArgument"));
        assertThat(theResult(), hasXPath("//o:error", "The 'from' date must be less then the 'until' one"));
    }
}