import React, { useEffect, useState } from "react";
import ReactPaginate from "react-paginate";
import "./MyItem.css";
import axios from "axios";
const apiUrl = "http://j7a606.p.ssafy.io:8080/"

function Eyes({ itemsPerPage, setUrl, setEyeId }) {
  const [data, setData] = useState([]);
  const [len, setLen]=useState(0);
  const handleInfo = (data, len) => {
    const result= [];
    for(let i = 0; i < len; i++){
      if(data[i].type === 2){
        result.push(data[i])
      }
    }
    setData(result);
    setLen(result.length)
  }
  const [currentItems, setCurrentItems] = useState([]);
  const [pageCount, setPageCount] = useState(0);
  const [itemOffset, setItemOffset] = useState(0);
  useEffect(()=> {
    const endOffset = itemOffset + itemsPerPage;
    axios.get(apiUrl+"api/item/inventory", {headers :{
      Authorization: "Bearer "+ window.localStorage.getItem("token")
    }}).then(res => handleInfo(res.data, res.data.length))

  },[])
  

  useEffect(() => {
    const endOffset = itemOffset + itemsPerPage;
    console.log(`Loading items from ${itemOffset} to ${endOffset}`);
    setCurrentItems(data.slice(itemOffset, endOffset));

    setPageCount(Math.ceil(data.length / itemsPerPage));
   
  }, [itemOffset, itemsPerPage, data]);

  const handlePageClick = (event) => {
    const newOffset = (event.selected * itemsPerPage) % data.length;
    console.log(
      `User requested page number ${event.selected}, which is offset ${newOffset}`
    );

    setItemOffset(newOffset);
  };


  return (
    <div className="inventory">
      <div className="items">
      {currentItems.map((info, idx)=> (<img className="parts" key={idx} src={`${process.env.PUBLIC_URL}/assets/images/items/Eye/02_${info.number}_${info.rgb}_${info.rare}.png`} onClick={()=> {setUrl(`${process.env.PUBLIC_URL}/assets/images/items/Head/01_${info.number}_${info.rgb}_${info.rare}.png`); setEyeId(info.itemId)}}/>))}
      </div>
      <ReactPaginate
        className="paginate"
        breakLabel="..."
        nextLabel=">"
        onPageChange={handlePageClick}
        pageRangeDisplayed={1}
        pageCount={pageCount}
        previousLabel="<"
        renderOnZeroPageCount={null}
      />
    </div>
  );
}

export default Eyes;
