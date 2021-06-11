import React, { useState, useEffect } from 'react';
import { Switch, Row, Col, Divider } from 'antd';
import { getDegradeList, degrade } from '../../common/api';

const Page = () => {
  const [domains, setDomains] = useState([]);

  useEffect(() => {
    getDegradeList().then(data => {
      const _domains = [];
      Object.keys(data).forEach(key => {
        _domains.push({
          label: key,
          checked: data[key]
        })
      })
      setDomains(_domains);
    });
  }, []);

  const doDegrade = (domain, checked) => {
    degrade(domain, checked).then(res => {
      getDegradeList().then(data => {
        const _domains = [];
        Object.keys(data).forEach(key => {
          _domains.push({
            label: key,
            checked: data[key]
          })
        })
        setDomains(_domains);
      });
    });
  }

  return (
    <>
      <div className="tab-3-body">
        <Row>
          <Col flex="auto" style={{ paddingLeft: 12, fontWeight: 'bolder' }}>Domain</Col>
          <Col flex="200px" style={{ fontWeight: 'bolder' }}>状态</Col>
        </Row>
        <Divider orientation="left"></Divider>
        {
          domains.map((item, index) => (
            <>
              <Row key={`tab3_switch_${index}`}>
                <Col flex="auto" style={{ paddingLeft: 12 }}>{item.label}</Col>
                <Col flex="200px">
                  <Switch
                    checkedChildren="降级"
                    unCheckedChildren="正常"
                    checked={item.checked}
                    onChange={checked => {
                      console.log(checked, item.label)
                      doDegrade(item.label, checked);
                    }}
                  />
                </Col>
              </Row>
              <Divider orientation="left"></Divider>
            </>
          ))
        }
      </div>
      <div>
        
      </div>
    </>
  )
}

export default Page;