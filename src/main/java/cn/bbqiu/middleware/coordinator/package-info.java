/**
 * @author: chengxin chengxin@zbj.com
 * @date: 19-1-21
 * @time: 下午10:22
 * @Description: v0.0.1 采用节点抢占的方式去竞争任务，太过于依赖zk，故而放弃；当前版本采用一致性hash/hash环进行任务均很分配
 */

package cn.bbqiu.middleware.coordinator;