package com.platform.familytree.shared.web;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class BetaGateFilterTest {

    private final FilterChain chain = mock(FilterChain.class);

    @Test
    void gateIsOffWhenNoPasscodeIsConfigured() throws Exception {
        BetaGateFilter filter = new BetaGateFilter("");
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/archive");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void apiCallWithoutPasscodeIsRejected() throws Exception {
        BetaGateFilter filter = new BetaGateFilter("four-word-pass");
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/archive");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, chain);

        verifyNoInteractions(chain);
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("BETA_REQUIRED");
        assertThat(response.getHeader("Cache-Control")).isEqualTo("no-store");
    }

    @Test
    void apiCallWithWrongPasscodeIsRejected() throws Exception {
        BetaGateFilter filter = new BetaGateFilter("four-word-pass");
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/archive");
        request.addHeader(BetaGateFilter.HEADER, "not-the-pass");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, chain);

        verifyNoInteractions(chain);
        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    void apiCallWithCorrectPasscodePasses() throws Exception {
        BetaGateFilter filter = new BetaGateFilter("four-word-pass");
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/archive");
        request.addHeader(BetaGateFilter.HEADER, "four-word-pass");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    void staticFilesStayPublicSoThePasscodeScreenCanLoad() throws Exception {
        BetaGateFilter filter = new BetaGateFilter("four-word-pass");
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/index.html");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }
}
