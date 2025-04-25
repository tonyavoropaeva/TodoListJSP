<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>TODO List JSP</title>
</head>
<body>
<h1>TODO List JSP</h1>

<form action="${pageContext.request.contextPath}/" method="POST">
    <input name="text" type="text">
    <button name="action" value="create" type="submit">Create</button>
    <c:if test="${not empty createError}">
        <div>${createError}</div>
    </c:if>
</form>
<ul>
    <c:forEach var="item" items="${todoItems}">
        <li>
            <form action="${pageContext.request.contextPath}/" method="POST">
                <input type="text" name="text" value="${item.text}">
                <button name="action" value="update" type="submit">Save</button>
                <button name="action" value="delete" type="submit">Delete</button>
                <input type="hidden" name="id" value="${item.id}">
                <c:if test="${not empty requestScope['updateError_'.concat(item.id)]}">
                    <div>${requestScope['updateError_'.concat(item.id)]}</div>
                </c:if>
            </form>
        </li>
    </c:forEach>
</ul>
</body>
</html>
