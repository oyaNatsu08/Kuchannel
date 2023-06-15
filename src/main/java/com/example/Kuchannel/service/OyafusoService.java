package com.example.Kuchannel.service;

import com.example.Kuchannel.dao.KuchannelDao;
import com.example.Kuchannel.entity.CommunityRecord;
import com.example.Kuchannel.entity.UrlRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OyafusoService implements KuchannelService {

    @Autowired
    private KuchannelDao oyafusoDao;

    //urlが重複しないかチェック
    @Override
    public CommunityRecord checkUrl(String str, String name) {
        return oyafusoDao.checkUrl(str, name);
    }

    //コミュニティテーブルインサート処理
    @Override
    public int communityInsert(String name, String url) {
        return oyafusoDao.communityInsert(name, url);
    }

    //コミュニティユーザーテーブルインサート処理
    @Override
    public int communityUserInsert(Integer userId, Integer communityId, String nickName, Integer role) {
        return oyafusoDao.communityUserInsert(userId, communityId, nickName, role);
    }

    @Override
    public UrlRecord getUrl(Integer id) {
        return oyafusoDao.getUrl(id);
    }

}
