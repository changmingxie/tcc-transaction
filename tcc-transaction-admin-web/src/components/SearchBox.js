import React, { useState } from 'react';
import { Button, Cascader } from 'antd';

const SearchBox = ({
  onChange,
  onSearch,
  options
}) => {
  const [selectedOptions, setSelectedOptions] = useState([]);

  const handleSearch = () => {
    onSearch(...selectedOptions);
  };

  const handleAdd = () => {}
  
  return (
    <div className="search-box">
      <Cascader
        style={{ width: 600 }}
        placeholder="请选择"
        options={options}
        onChange={(val, selectedOptions) => {
          const [domain, row] = selectedOptions.reduce((prev, val) => prev.concat(val.label), []);
          setSelectedOptions([domain, row]);
          onChange(domain, row);
        }}
        showSearch
      />
      <Button type="primary" onClick={handleSearch}>搜索</Button>
      <Button type="primary" onClick={handleAdd} disabled>添加</Button>
    </div>
  )
}

export default SearchBox;