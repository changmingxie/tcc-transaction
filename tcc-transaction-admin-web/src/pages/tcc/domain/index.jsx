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
    api.getDomains().then((res) => {
      dispatch({type: Domain.UPDATE_DOMAIN_DATA, payload: res});
    });
  };

  useEffect(() => {
    getDomainList();
  }, []);

  const onTabChange = (tab) => {
    setActiveTabKey(tab);
  };

  return (
    <React.Fragment>
      <SearchBox/>
      <div className="content">
        <Tabs
          defaultActiveKey="normal"
          onChange={onTabChange}
          activeKey={activeTabKey}
        >
          <TabPane tab="Normal" key="normal"/>
          <TabPane tab="Deleted Keys" key="deletedKeys"/>
        </Tabs>
        {domainData.length && domainData
          .find((val) => val.label === domain)
          ?.children
          .map((row) => {
            return (
              <TableCard key={domain.concat(row.label)} row={row.label} activeTabKey={activeTabKey}/>
            );
          })}
      </div>
    </React.Fragment>
  );
};

export default Page;
