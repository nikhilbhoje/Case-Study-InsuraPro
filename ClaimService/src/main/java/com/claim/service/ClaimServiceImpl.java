package com.claim.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.claim.exception.ClaimRejectException;
import com.claim.model.Claim;
import com.claim.model.ClaimDto;
import com.claim.repository.ClaimRepository;

@Service
public class ClaimServiceImpl implements ClaimService{
	
	@Autowired
	private ClaimRepository claimRepository;
	
	@Autowired
	private RestTemplate restTemplate;
	
	
	public String claim(int userId, int policyId) {
		String policyUrl = "http://paymentservice"+policyId;
		ClaimDto expiryDate = restTemplate.getForObject(policyUrl, ClaimDto.class);
		String status = "";
		
		Claim claim = new Claim();
		
		if(LocalDate.now().isAfter(expiryDate.getExpiryDate())) {
			status = "Policy is Expired";
			claim.setStatus("Policy is Expired");
		}
		else {
			claim.setStatus("Policy is Not Expired");
		}
		claim.setPolicyId(policyId);
		claim.setUserId(userId);
		claim.setStatus(status);
		claimRepository.save(claim);
		
		if(claimRepository.existsById(claim.getClaimId())) {
			return "Your Claim Request is received, we will shortly inform you regarding your claim!!";
		}
		throw new ClaimRejectException("Your Claim is rejected because your policy has expired!!");
	}

	@Override
	public List<Claim> showAllClaim() {
		List<Claim> claimList = claimRepository.findAll();
		return claimList;
	}

	
}
