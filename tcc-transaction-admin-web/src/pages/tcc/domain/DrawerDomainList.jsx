import React from "react";
import {useDispatch, useSelector} from "react-redux";
import {Domain} from "../../../store/actions/domain";
import * as api from "../../../common/api";
import {Button, Col, message, Popconfirm, Row} from "antd";

const DrawerDomainList = () => {
  const { domainData } = useSelector(
    ({ domain }) => domain
  );
  const dispatch = useDispatch();

  //获取domain数据
  const getDomainList = () => {
    api.getDomains().then((res) => {
      dispatch({type: Domain.UPDATE_DOMAIN_DATA, payload: res});
    });
  };

  const confirmAgain = (value) => {
    api.deleteDomain(value).then((res) => {
      if(res.data.code === 200) {
        message.success("删除成功");
        getDomainList()
      } else {
        message.success("出错了，请联系管理员");
      }
    });
  };

  return (
    <>
      {domainData.map((item, index) => {
        return (
          <Row key={index} align="middle" justify="end">
            <Col span={20}>{item.value}</Col>
            <Col span={4}>
              <Popconfirm
                title={`确定要删除${item.label}吗？`}
                onConfirm={() => confirmAgain(item.value)}
                okText="确定"
                cancelText="取消"
              >
                <Button danger type="text">
                  删除
                </Button>
              </Popconfirm>
            </Col>
          </Row>
        );
      })}
    </>
  );
};

export default DrawerDomainList;
