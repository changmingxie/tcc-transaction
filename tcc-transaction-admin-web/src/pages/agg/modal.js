import React from "react";
import { Form, Input, message, Modal } from "antd";
import * as api from "../../api/agg";


const CreateModal = ({
  handleCancel,
  getList,
}) => {
  const [form] = Form.useForm();

  const onFinish = (values) => {
    form
      .validateFields()
      .then((values) => {
        let data = {
          database: values.database,
          password: values.password,
          port: values.port,
          domain: values.domain,
          host: values.host,
          application: values.application,
          owners: [{ name: values.owners }],
        };
        /**
         *
         */
        api
          .handleToAdd(data)
          .then((res) => {
            if (+res.data.code === 200) {
              message.success("新增成功");
              handleCancel();
              getList();
            } else {
              message.error(res.data.message);
            }
          })
          .catch((res) => {});
      })
      .catch((info) => {
        console.log("Validate Failed:", info);
      });
  };

  return (
    <React.Fragment>
      <Modal
        getContainer={false}
        title="添加"
        visible={true}
        onOk={onFinish}
        onCancel={handleCancel}
        cancelText="关闭"
        okText="提交更改"
      >
        <Form form={form}>
          <Form.Item
            label="database"
            name="database"
            rules={[
              {
                required: true,
                message: "请输入",
              },
            ]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            label="端口"
            name="port"
            rules={[
              {
                required: true,
                message: "请输入",
              },
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="密码"
            name="password"
            rules={[
              {
                required: true,
                message: "请输入",
              },
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="domain"
            name="domain"
            rules={[
              {
                required: true,
                message: "请输入",
              },
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="host"
            name="host"
            rules={[
              {
                required: true,
                message: "请输入",
              },
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="application"
            name="application"
            rules={[
              {
                required: false,
                message: "请输入",
              },
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="owners"
            name="owners"
            rules={[
              {
                required: false,
                message: "请输入",
              },
            ]}
          >
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </React.Fragment>
  );
};

export default CreateModal;
