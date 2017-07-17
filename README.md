FileManagement
User表
字段	类型长度	      说明
id	  int	          用户ID
salt	char(8)	      随机字符串
name	varchar(32)	  用户名
pwd	  varchar(32)	  哈希(用户名+salt+密码用MD5加密)
limitspace	int	    可使用空间大小
space	      int     所属空间大小
parent	    int     第一级为null，下面一级为上一级ID
stauts	    int	    用户状态：
                          -1 禁用
                           0 正常

File表
字段	类型长度	      说明
id	    int	        文件ID
userid	int	        文件所属用户
hash	varchar(32)	  文件的MD5值
sourcename	varchar(255)	源文件名：源
targetname	varchar(255)	新文件名：UUID
mime	varchar(255)	      文件类型
lasttime	date	     最后上传日期时间
size	long	         文件大小(B)
status	int        	文件状态：
                        -1 已经删除
                        0  上传中
                        1  上传完成

注：文件所有者可操作文件，上级可以下载下级文件，但不能操作，上级可以删除下级用户
checkLogin
login?username=admin&password=123456

userManger?action=getList&id=0

userManger?action=update&name=admin12&space=20&userid=2&id=1

userManger?action=password&userid=12&id=12&pwd=123456

userManger?action=add&name=admin13&space=100000
