/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:myinvestor-backend@MyInvestor.es
 *
 * fraud-detection-engine - Created by pedro.almendro@MyInvestor
 * Date: 6/8/25 Time: 10:13
 *
 */
package io.bitnomio.fde.infra.io.rest;

//import es.myinvestor.fde.application.EvaluateTransactionReq;
//import es.myinvestor.fde.domain.actions.EvaluateTransactionAction;
//import es.myinvestor.fde.infrastructure.io.rest.res.FraudDecisionResponse;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import jakarta.validation.Valid;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fraud")
public class FraudEvaluationController {

//  private final EvaluateTransactionAction evaluateTransactionAction;
//
//  public FraudEvaluationRestController(EvaluateTransactionAction evaluateTransactionAction) {
//    this.evaluateTransactionAction = evaluateTransactionAction;
//  }
//
//  @Operation(summary = "Evaluate a transaction for fraud",
//             responses = {
//                 @ApiResponse(responseCode = "200", description = "Transaction evaluated successfully",
//                              content = @Content(schema = @Schema(implementation = FraudDecisionResponse.class))), // DOCUMENT the direct DTO
//                 @ApiResponse(responseCode = "400", description = "Invalid request payload",
//                              content = @Content(schema = @Schema(implementation = Map.class))), // Document the raw error map
//                 @ApiResponse(responseCode = "500", description = "Internal server error",
//                              content = @Content(schema = @Schema(implementation = Map.class)))
//             })
//  @PostMapping("/evaluate")
//  public ResponseEntity<FraudDecisionResponse> evaluateTransaction(
//      @Valid @RequestBody EvaluateTransactionReq request,
//      Principal principal // User Principal is injected by Spring Security context if configured
//  ) {
//    // The application service returns the raw DTO
//    FraudDecisionResponse responseDto = evaluateTransactionAction.evaluate(request);
//
//    // Return the DTO directly with a 200 OK status
//    return ResponseEntity.ok(responseDto);
//  }
}
