import React, {useEffect, useState} from 'react';
import {Tabs} from 'antd';
import * as api from '../../../common/api';
import SearchBox from './SearchBox';
import {useDispatch, useSelector} from 'react-redux';
import {Domain} from '../../../store/actions/domain';
import TableCard from './TableCard';

const {TabPane} = Tabs;

const Page = () => {
  const [activeTabKey, setActiveTabKey] = useState('normal');
  const {currentDomain: domain, domainData} = useSelector(({domain}) => domain);
  const dispatch = useDispatch();

  //获取domain数据
  const getDomainList = () => {
    api.getAllDomainKeys().then((res) => {
      dispatch({type: Domain.UPDATE_DOMAIN_DATA, payload: res});
    });
  };

  useEffect(() => {
    getDomainList();
  }, []);

  const onTabChange = (tab) => {
    console.log("tabPane onTabChange", tab, domain)
    setActiveTabKey(tab);
  };

  return (
    <React.Fragment>
      <SearchBox domain={domain}/>
      <div className="content">
        <Tabs
          defaultActiveKey="normal"
          onChange={onTabChange}
          activeKey={activeTabKey}
        >
          <TabPane tab="Normal" key="normal"/>
          <TabPane tab="Deleted Keys" key="deletedKeys"/>
        </Tabs>
        <TableCard key={domain} activeTabKey={activeTabKey}/>
      </div>
    </React.Fragment>
  );
};

export default Page;
