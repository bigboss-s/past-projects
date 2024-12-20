"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.QuestionsModule = void 0;
const common_1 = require("@nestjs/common");
const questions_service_1 = require("./questions.service");
const questions_resolver_1 = require("./questions.resolver");
const typeorm_1 = require("@nestjs/typeorm");
const questions_entity_1 = require("./questions.entity");
const answers_module_1 = require("../answers/answers.module");
let QuestionsModule = class QuestionsModule {
};
exports.QuestionsModule = QuestionsModule;
exports.QuestionsModule = QuestionsModule = __decorate([
    (0, common_1.Module)({
        imports: [typeorm_1.TypeOrmModule.forFeature([questions_entity_1.Question]), answers_module_1.AnswersModule],
        providers: [questions_service_1.QuestionsService, questions_resolver_1.QuestionsResolver],
        exports: [questions_service_1.QuestionsService, typeorm_1.TypeOrmModule.forFeature([questions_entity_1.Question])]
    })
], QuestionsModule);
//# sourceMappingURL=questions.module.js.map