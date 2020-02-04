/*
Navicat MySQL Data Transfer

Source Server         : mysql-zbh-local
Source Server Version : 50722
Source Host           : localhost:3306
Source Database       : buybuy

Target Server Type    : MYSQL
Target Server Version : 50722
File Encoding         : 65001

Date: 2020-02-03 17:04:07
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for buy_order
-- ----------------------------
DROP TABLE IF EXISTS `buy_order`;
CREATE TABLE `buy_order` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint(15) NOT NULL,
  `order_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of buy_order
-- ----------------------------

-- ----------------------------
-- Table structure for buy_product
-- ----------------------------
DROP TABLE IF EXISTS `buy_product`;
CREATE TABLE `buy_product` (
  `id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `buy_stock` int(11) NOT NULL,
  `buy_price` double(10,2) NOT NULL,
  `start_date` datetime NOT NULL,
  `end_date` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of buy_product
-- ----------------------------
INSERT INTO `buy_product` VALUES ('101', '1001', '10', '3333.00', '2020-01-29 20:11:00', '2020-01-30 16:42:00');
INSERT INTO `buy_product` VALUES ('102', '1002', '10', '4999.00', '2020-01-01 09:07:55', '2020-02-22 09:08:08');

-- ----------------------------
-- Table structure for order_detail
-- ----------------------------
DROP TABLE IF EXISTS `order_detail`;
CREATE TABLE `order_detail` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint(15) NOT NULL,
  `product_id` int(11) NOT NULL,
  `address` varchar(255) NOT NULL,
  `product_name` varchar(255) NOT NULL,
  `product_count` int(11) NOT NULL,
  `product_price` double(10,2) NOT NULL,
  `status` int(11) NOT NULL,
  `create_date` datetime NOT NULL,
  `pay_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of order_detail
-- ----------------------------

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `product_name` varchar(255) NOT NULL,
  `product_price` double(10,2) NOT NULL,
  `product_detail` varchar(255) NOT NULL,
  `product_img` varchar(255) NOT NULL,
  `product_title` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1003 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES ('1001', 'HUAWEI P30 Pro', '3999.00', '全新双重质感及配色，诠释唯美设计新可能：\n深邃的墨玉蓝如同海水映射的神秘夜空，浪漫的嫣紫色仿佛让你置身梦幻海滩，\n高光、雾面顺滑过渡，成就质感与触感的浑然天成。\nHUAWE丨P30Pro，以独特工艺定格时空变幻的光影艺术。', '/img/p30.png', 'HUAWEI P30 Pro 麒麟980 超感光徕卡四摄 屏内指纹 双景录像 8GB+128GB 全网通版（天空之境）');
INSERT INTO `product` VALUES ('1002', 'iPhone 11', '5199.00', '六种亮眼的新外观。绚丽的6.1英寸\nLiquid视网膜LCD全面屏2.\n抗水性能出色，可在2米水深\n停留30分钟。', '/img/iPhone.png', 'Apple 苹果 iPhone 11 移动联通电信4G手机 双卡双待 黑色 64GB');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(15) NOT NULL,
  `nickname` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `salt` varchar(255) NOT NULL,
  `login_count` int(11) NOT NULL,
  `register_date` datetime NOT NULL,
  `last_login_date` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('13720201236', 'reck', '7b8b85447acdf5159b9f663a2c0992d1', 'wuhui666', '0', '2020-01-23 05:40:58', '2020-01-23 05:40:58');
