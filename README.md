# CommandWhitelist

## 插件特点
以白名单的方式阻止命令以及Tab补全 相比黑名单可以减少更多的配置  
全版本支持 支持1.7.10以上的游戏版本 自动适配  
分别控制命令白名单和补全白名单 实现一些不可补全的隐藏命令  
权限控制 给予特定权限后使不同用户拥有不一样的白名单  
分组控制 分成多组给予不同的权限组或用户 省去配置权限的麻烦  
有效地去除大面积的 /bukkit:xxxxx /essentials:xxxxx  
也能防止玩家使用 /about \<tab>,/help \<tab>知晓服务器所安装的插件

## 使用方式
开箱即用

## 命令
*   /commandwhitelist or /cmdw 主命令
*   /<主命令> reload 重载命令
*   /<主命令> help 帮助列表

## 权限
*   commandwhitelist.allow.command 允许使用命令(无视白名单)
*   commandwhitelist.allow.command.\<cmd> 允许使用该命令(加入白名单)
*   commandwhitelist.allow.tabcomplete 允许补全命令(无视白名单)
*   commandwhitelist.allow.tabcomplete.\<cmd> 允许补全该命令(加入白名单)
*   commandwhitelist.group.command.\<group> 允许使用该组的命令
*   commandwhitelist.group.tabcomplete.\<group> 允许补全该组的命令
*   commandwhitelist.command 允许使用本插件的命令
*   commandwhitelist.command.\<subcmd> 允许使用本插件的该子命令
*   commandwhitelist.command.* 允许使用本插件的所有子命令

## 配置
主配置文件
~~~yaml
# 命令白名单
command-whitelist:
  # 启用
  enable: true
  # 阻止后发送的消息
  blocked-message: '&cNot Command.'
  # 列表 (&是Yaml的锚点符)
  list: &cmdlist
    - hehe
    - haha
    - help

# Tab补全白名单
tab-complete-whitelist:
  # 启用
  enable: true
  # 使用ProtocolLib(拦截数据包) 1.13以下必须开启
  protocol-lib: true
  # 列表 (与命令白名单相同)
  list: *cmdlist
~~~
权限配置文件
~~~yaml
# 分组
# 给予玩家权限 commandwhitelist.group.command.<组名> 即可给予玩家该组的命令白名单
# 给予玩家权限 commandwhitelist.group.tabcomplete.<组名> 即可给予玩家该组的Tab补全白名单
group:
  # 用户组
  user:
    - abcd
    - efgh
    - hizj
  # 会员组
  vip:
    - fly
    - heal
  # 管理组
  admin:
    - plugins
    - about
~~~
详细权限配置
~~~yaml
permissions:
  commandwhitelist.allow.command:
    description: '绕过使用命令白名单'
    default: false
  commandwhitelist.allow.tabcomplete:
    description: '绕过Tab补全白名单'
    default: false
  commandwhitelist.allow.*:
    description: '绕过所有白名单'
    default: op
    children:
      commandwhitelist.allow.command: true
      commandwhitelist.allow.tabcomplete: true
  commandwhitelist.command:
    description: '允许使用CommandWhitelist插件的命令'
    default: false
  commandwhitelist.command.reload:
    description: '允许使用CommandWhitelist插件的重载命令'
    default: false
  commandwhitelist.command.help:
    description: '允许使用CommandWhitelist插件的帮助命令'
    default: true
  commandwhitelist.command.*:
    description: '允许使用CommandWhitelist插件的所有命令'
    default: false
    children:
      commandwhitelist.command.reload: true
      commandwhitelist.command.help: true
~~~

## 效果展示
![低版本不使用CWL的效果](/docs/img/低版本不使用CWL的效果.png)
![低版本使用CWL的效果](/docs/img/低版本使用CWL的效果.png)
![高版本不使用CWL的效果](/docs/img/高版本不使用CWL的效果.png)
![高版本使用CWL的效果](/docs/img/高版本使用CWL的效果.png)

## 更新日志
\* 着重符  
v1.0.1:  
\- 新增 添加1.15.x的支持  
v1.1.0:  
\- 备注* 大更新 需要完全删除旧版本 并重新配置  
\- 新增 自动检测版本 后续版本不需要再添加兼容性了 支持1.8-1.16-以后  
\- 修改* 权限节点名称修改 详见本贴新的权限节点  
\- 修改* 命令修改 详见本贴新的命令节点  
\- 修改* 配置文件修改 配置文件内容结构完全不同  
\- 备注* 开源 [Github](https://github.com/msgdw/CommandWhitelist)  
v1.1.1:  
\- 新增* 分组模式 方便配置不用再给一大堆权限了  
v1.1.2:  
\- 修复 跨版本客户端包括(Minecraft Console Client, ProtocolSupport, PickaxeChat)拦截补全的问题  
v1.1.3:
\- 修复 命令白名单与补全白名单相同的问题  
v1.1.4:
\- 修改 默认权限设定 op将获得重载命令的默认权限  
v1.2.0:
\- 新增 多条命令 用于在游戏内添加移除白名单  
\- 修改 现在重载插件也会重新发包给在线玩家了  

## 已知问题
1. 在1.13+版本以上 重载后 补全依旧不变  
高版本客户端中命令补全进行了修改  
现在客户端对命令补全增加了缓存  
重载后需要重启客户端才能生效  
2. 将cmi的命令加入白名单中时 必须将cmi加入  
cmi的命令别名 包括cmi的命令补全  
都需要将cmi加入到白名单才行  