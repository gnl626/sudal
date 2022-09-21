package com.a606.api.service;

import com.a606.api.dto.MyNFTDto;
import com.a606.contract.SudalFarm;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service("contractService")
public class ContractServiceImpl implements ContractService{

//    로컬 테스트
//    private String contract = "0xD732B50c0490965279C482fa5C44B358A0467f32";
//    private String key = "7de9b804253e76a421515313536b138ca1addd3e9aaaf07152d9155b71edaafa";
//    private String url = "http://localhost:7545";

//    싸피 네트워크
    private String contract = "0xCC7e82114aF80c36Bb815a92BF85a6A8f9155188";
    private String key = "2c835aeb997e4d1269513a83efadc44de96a30a56c1b58cb7f8ce8897511c79f";
    private String url = "http://20.196.209.2:8545";

    @Override
    public List<MyNFTDto> getNFTbyAddress(String Address) throws Exception {
        Web3j web3j = Web3j.build(new HttpService(url));
        Credentials credentials = Credentials.create(key);
        ContractGasProvider gasProvider = new DefaultGasProvider();
        FastRawTransactionManager manager = new FastRawTransactionManager(web3j, credentials,
                new PollingTransactionReceiptProcessor(web3j, 3000, 3));

        SudalFarm sudalFarm = SudalFarm.load(contract, web3j, manager, gasProvider);

        Tuple3 tuple3 = sudalFarm.getSudalsByOwner(Address).send();

        List<String> names = (List<String>) tuple3.component1();
        List<BigInteger> ids = (List<BigInteger>) tuple3.component2();
        List<BigInteger> dnas = (List<BigInteger>) tuple3.component3();

        List<MyNFTDto> mynfts = new ArrayList<MyNFTDto>();

        for(int i = 0; i < names.size(); i++){
            MyNFTDto mynft = new MyNFTDto();
            mynft.setTokenId(String.valueOf(ids.get(i)));
            mynft.setTokenURI(sudalFarm.tokenURI(ids.get(i)).send());
            mynfts.add(mynft);
        }

        return mynfts;
    }

    @Override
    public String getAddressbyTokenId(String tokenId) throws Exception {
        Web3j web3j = Web3j.build(new HttpService(url));
        Credentials credentials = Credentials.create(key);
        ContractGasProvider gasProvider = new DefaultGasProvider();
        FastRawTransactionManager manager = new FastRawTransactionManager(web3j, credentials,
                new PollingTransactionReceiptProcessor(web3j, 3000, 3));

        SudalFarm sudalFarm = SudalFarm.load(contract, web3j, manager, gasProvider);

        String address = sudalFarm.ownerOf(new BigInteger(tokenId)).send();

        return address;
    }

}
