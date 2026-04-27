package com.example.demo.dao;

import com.example.demo.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import java.util.*;
@Mapper
public interface MessageMapper {
    // 查询当前用户的会话列表，每个会话只返回一条最新的私信
    List<Message> selectConversations(int userId, int offset, int limit);

    // 查询当前用户的会话数量
    int selectConversationCount(int userId);

    // 查询某个会话包含的私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);

    // 查询某个会话包含的私信数量
    int selectLetterCount(String conversationId);

    // 查询未读私信的数量 (如果是总未读，conversationId 传 null；如果是某个会话未读，传具体 ID)
    int selectLetterUnreadCount(int userId, String conversationId);

    // 新增私信
    int insertMessage(Message message);

    // 修改读取状态 (为后续详情页准备)
    int updateStatus(List<Integer> ids, int status);

}