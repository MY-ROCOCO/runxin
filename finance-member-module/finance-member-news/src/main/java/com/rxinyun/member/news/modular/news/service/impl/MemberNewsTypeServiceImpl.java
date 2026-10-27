package com.rxinyun.member.news.modular.news.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.member.news.modular.news.mapper.MemberNewsTypeMapper;
import com.rxinyun.member.news.modular.news.model.po.NewsType;
import com.rxinyun.member.news.modular.news.service.NewsTypeService;
import org.springframework.stereotype.Service;

@Service
public class MemberNewsTypeServiceImpl extends ServiceImpl<MemberNewsTypeMapper, NewsType> implements NewsTypeService {
}
