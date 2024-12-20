"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.ReturnQuestionDTO = void 0;
const graphql_1 = require("@nestjs/graphql");
const return_answer_input_1 = require("../../answers/dto/return-answer.input");
const questions_entity_1 = require("../questions.entity");
let ReturnQuestionDTO = class ReturnQuestionDTO {
};
exports.ReturnQuestionDTO = ReturnQuestionDTO;
__decorate([
    (0, graphql_1.Field)(type => graphql_1.Int),
    __metadata("design:type", Number)
], ReturnQuestionDTO.prototype, "id", void 0);
__decorate([
    (0, graphql_1.Field)(type => questions_entity_1.QuestionType),
    __metadata("design:type", String)
], ReturnQuestionDTO.prototype, "type", void 0);
__decorate([
    (0, graphql_1.Field)(type => [return_answer_input_1.ReturnAnswerDTO]),
    __metadata("design:type", Array)
], ReturnQuestionDTO.prototype, "answers", void 0);
exports.ReturnQuestionDTO = ReturnQuestionDTO = __decorate([
    (0, graphql_1.InputType)()
], ReturnQuestionDTO);
//# sourceMappingURL=return-question.input.js.map