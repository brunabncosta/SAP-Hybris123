<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="hac" uri="/WEB-INF/custom.tld" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="url"><c:url value="/platform/log4j/changeLevel/"/></c:set>
<sec:authorize access="hasAnyRole('ROLE_ADMINGROUP', 'ROLE_HAC_PLATFORM_LOGGING')" var="canChangeLoggers" />

<html>
<head>
<title>Logging Configuration</title>
<link rel="stylesheet" href="<c:url value="/static/css/table.css"/>" type="text/css" media="screen, projection">

<script type="text/javascript" src="<c:url value="/static/js/jquery.dataTables.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/static/js/history.js"/>"></script>
<script type="text/javascript" src="<c:url value="/static/js/platform/log4j.js"/>"></script>

</head>
<body>
	<div class="prepend-top span-17 colborder" id="content">
		<button id="toggleSidebarButton">&gt;</button>
		<div class="marginLeft" id="inner">
			<h2>Logging Configuration</h2>

            <c:if test="${canChangeLoggers}">
                <table id="configureLogger">
                    <thead>
                        <tr>
                            <th style="background:#dedede !important;padding: 4px 15px 4px 5px;">
                                <input type="text" id="loggerToConfigure" style="width:100%;" placeholder="Logger..."/>
                            </th>
                            <th style="background:#dedede !important;border-left-style:solid;border-left-width:1px;border-left-color:#bcbcbc;width:145px">
                                <div style="text-align:center;vertical-align:middle">
                                    <div style="float:left">
                                        <select id="logLevelToSet">
                                            <c:forEach items="${levels}" var="level">
                                                <option value="${fn:escapeXml(level)}">${fn:escapeXml(level)}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div style="float:right">
                                        <button id="configureLogLevelButton">configure</button>
                                    </div>
                                </div>
                            </th>

                        </tr>
                    </thead>
                </table>
            </c:if>
			<table id="loggers" data-changeLoggerLevelUrl="${url}">
				<thead>
					<tr>
						<th>Logger</th>
						<th>Parent Logger</th>
						<th>Effective Level</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${loggers}" var="logger">
						<tr>
							<td title="${fn:escapeXml(logger.name)}">${fn:escapeXml(logger.name)}</td>
							<td title="${fn:escapeXml(logger.parentName)}">${fn:escapeXml(logger.parentName)}</td>
							<td>
                                <c:choose>
                                    <c:when test="${canChangeLoggers}">
                                        <select class="loggerLevels" data-loggerName="${fn:escapeXml(logger.name)}">
                                            <c:forEach items="${levels}" var="level">
                                                <c:choose>
                                                    <c:when test="${logger.effectiveLevel == level}">
                                                        <option value="${fn:escapeXml(level)}" selected="selected">${fn:escapeXml(level)}</option>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <option value="${fn:escapeXml(level)}">${fn:escapeXml(level)}</option>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>
                                        </select>
                                    </c:when>
                                    <c:otherwise>
                                        ${fn:escapeXml(logger.effectiveLevel)}
                                    </c:otherwise>
                                </c:choose>

							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
	<div class="span-6 last" id="sidebar">
		<div class="prepend-top" id="recent-reviews">
			<h3 class="caps">Page description</h3>
			<div class="box">
				<div class="quiet">
					This page enables you to change the log levels for log4j utility
					<hr />
					<hac:note>
					All your changes are stored only in the memory. It means that after restarting the application server the default settings 
					are restored. To make your changes persistent, enter them in the local.properties file directly.
					</hac:note>
				</div>
			</div>
			<h3 class="caps">
				Useful Links
			</h3>
			<div class="box">
				<div class="quiet">
					<ul>
					 <li> <a href="${wikiLog4j}" target="_blank" rel="noopener noreferrer" class="quiet" >Logging</a> </li>
					</ul>
				</div>
			</div>			
		</div>
	</div>
</body>
</html>

