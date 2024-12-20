"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.QuizzesModule = void 0;
const common_1 = require("@nestjs/common");
const quizzes_service_1 = require("./quizzes.service");
const quizzes_resolver_1 = require("./quizzes.resolver");
const typeorm_1 = require("@nestjs/typeorm");
const quiz_entity_1 = require("./quiz.entity");
const questions_module_1 = require("../questions/questions.module");
const answers_module_1 = require("../answers/answers.module");
let QuizzesModule = class QuizzesModule {
};
exports.QuizzesModule = QuizzesModule;
exports.QuizzesModule = QuizzesModule = __decorate([
    (0, common_1.Module)({
        imports: [typeorm_1.TypeOrmModule.forFeature([quiz_entity_1.Quiz]), questions_module_1.QuestionsModule, answers_module_1.AnswersModule],
        providers: [quizzes_service_1.QuizzesService, quizzes_resolver_1.QuizzesResolver]
    })
], QuizzesModule);
//# sourceMappingURL=quizzes.module.js.map