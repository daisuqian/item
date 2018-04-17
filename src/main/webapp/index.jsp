<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>批量导入客户</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
<div>批量导入客户</div>
<form action="/manage/teacher/batch_import_info.do" method="post"
      enctype="multipart/form-data"
      onsubmit="return check();">
    <div style="margin: 30px;">
        <input id="excel_file" type="file" name="filename"
               accept="xlsx" size="80"/>
        <input id="excel_button" type="submit" value="导入Excel"/>
    </div>
</form>
</body>
<script type="text/javascript">
    function check() {
        var excel_file = $("#excel_file").val();
        if (excel_file == "" || excel_file.length == 0) {
            alert("请选择文件路径！");
            return false;
        } else {
            return true;
        }
    }
</script>

</html>