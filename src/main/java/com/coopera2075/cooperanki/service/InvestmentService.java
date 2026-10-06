package com.coopera2075.cooperanki.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coopera2075.cooperanki.dto.InvestmentRequest;
import com.coopera2075.cooperanki.dto.InvestmentResponse;
import com.coopera2075.cooperanki.entity.AppUser;
import com.coopera2075.cooperanki.entity.CallProp;
import com.coopera2075.cooperanki.entity.CallStatus;
import com.coopera2075.cooperanki.entity.Funder;
import com.coopera2075.cooperanki.entity.Investment;
import com.coopera2075.cooperanki.entity.Manager;
import com.coopera2075.cooperanki.exception.ConflictException;
import com.coopera2075.cooperanki.exception.ResourceNotFoundException;
import com.coopera2075.cooperanki.repository.ArrangementRepository;
import com.coopera2075.cooperanki.repository.CallPropRepository;
import com.coopera2075.cooperanki.repository.InvestmentRepository;
import com.coopera2075.cooperanki.security.CurrentUserProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvestmentService {
    private final InvestmentRepository investmentRepository;
    private final CallPropRepository callPropRepository;
    private final ArrangementRepository arrangementRepository;
    private final CurrentUserProvider currentUserProvider;

    // Un funder ve sus propias inversiones; un manager, las recibidas en las convocatorias de sus proyectos
    @Transactional(readOnly = true)
    public List<InvestmentResponse> findAll(Long callPropId) {
        AppUser currentUser = currentUserProvider.getCurrentUser();

        List<Investment> investments = currentUser instanceof Manager
                ? investmentRepository.findByProjectManagerId(currentUser.getId())
                : investmentRepository.findByFunderId(currentUser.getId());

        return investments.stream()
                .filter(investment -> callPropId == null || investment.getCallProp().getId().equals(callPropId))
                .map(InvestmentResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public InvestmentResponse findById(Long id) {
        Investment investment = getInvestment(id);
        AppUser currentUser = currentUserProvider.getCurrentUser();

        boolean isOwner = investment.getFunder().getId().equals(currentUser.getId());
        boolean managesProject = currentUser instanceof Manager
                && arrangementRepository.existsByManagerIdAndProjectId(
                        currentUser.getId(), investment.getCallProp().getProject().getId());
        if (!isOwner && !managesProject) {
            throw new AccessDeniedException("No tienes acceso a esta inversión");
        }

        return InvestmentResponse.from(investment);
    }

    // El funder de la inversión es siempre el usuario autenticado
    @Transactional
    public InvestmentResponse create(Long callPropId, InvestmentRequest request) {
        Funder funder = currentUserProvider.getCurrentFunder();
        CallProp callProp = callPropRepository.findById(callPropId)
                .orElseThrow(() -> new ResourceNotFoundException("Convocatoria no encontrada con id: " + callPropId));
        requireAcceptingInvestments(callProp);

        Investment investment = new Investment();
        investment.setAmount(request.getAmount());
        investment.setFunder(funder);
        investment.setCallProp(callProp);

        return InvestmentResponse.from(investmentRepository.save(investment));
    }

    @Transactional
    public InvestmentResponse update(Long id, InvestmentRequest request) {
        Investment investment = getOwnInvestment(id);
        requireAcceptingInvestments(investment.getCallProp());

        investment.setAmount(request.getAmount());

        return InvestmentResponse.from(investment);
    }

    @Transactional
    public void delete(Long id) {
        Investment investment = getOwnInvestment(id);
        requireAcceptingInvestments(investment.getCallProp());

        investmentRepository.delete(investment);
    }

    // Las inversiones solo se pueden crear, modificar o retirar mientras la convocatoria siga vigente
    private void requireAcceptingInvestments(CallProp callProp) {
        if (callProp.getStatus() == CallStatus.CLOSED) {
            throw new ConflictException("La convocatoria está cerrada");
        }
        if (callProp.getDeadline() != null && callProp.getDeadline().isBefore(LocalDate.now())) {
            throw new ConflictException("La fecha límite de la convocatoria ya pasó");
        }
    }

    private Investment getOwnInvestment(Long id) {
        Investment investment = getInvestment(id);
        Funder funder = currentUserProvider.getCurrentFunder();
        if (!investment.getFunder().getId().equals(funder.getId())) {
            throw new AccessDeniedException("Esta inversión pertenece a otro usuario");
        }
        return investment;
    }

    private Investment getInvestment(Long id) {
        return investmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inversión no encontrada con id: " + id));
    }
}
