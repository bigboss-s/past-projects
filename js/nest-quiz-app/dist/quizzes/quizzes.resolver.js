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
var __param = (this && this.__param) || function (paramIndex, decorator) {
    return function (target, key) { decorator(target, key, paramIndex); }
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.QuizzesResolver = void 0;
const graphql_1 = require("@nestjs/graphql");
const quizzes_service_1 = require("./quizzes.service");
const quiz_entity_1 = require("./quiz.entity");
const create_quiz_input_1 = require("./dto/create-quiz.input");
const show_quiz_type_1 = require("./dto/show-quiz.type");
const return_quiz_input_1 = require("./dto/return-quiz.input");
const reusult_type_1 = require("./dto/reusult.type");
let QuizzesResolver = class QuizzesResolver {
    constructor(quizzesService) {
        this.quizzesService = quizzesService;
    }
    async getQuizzes() {
        return await this.quizzesService.findAll();
    }
    async createQuiz(createQuizInput) {
        return await this.quizzesService.createQuiz(createQuizInput);
    }
    async getFullQuiz(id) {
        return await this.quizzesService.findOne(id);
    }
    async showQuiz(id) {
        return await this.quizzesService.findOneToShow(id);
    }
    async checkQuiz(returnedQuiz) {
        return await this.quizzesService.checkQuiz(returnedQuiz);
    }
};
exports.QuizzesResolver = QuizzesResolver;
__decorate([
    (0, graphql_1.Query)(returns => [quiz_entity_1.Quiz]),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", []),
    __metadata("design:returntype", Promise)
], QuizzesResolver.prototype, "getQuizzes", null);
__decorate([
    (0, graphql_1.Mutation)(returns => quiz_entity_1.Quiz),
    __param(0, (0, graphql_1.Args)('CreateQuizInput')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [create_quiz_input_1.CreateQuizInput]),
    __metadata("design:returntype", Promise)
], QuizzesResolver.prototype, "createQuiz", null);
__decorate([
    (0, graphql_1.Query)(returns => quiz_entity_1.Quiz),
    __param(0, (0, graphql_1.Args)('id', { type: () => graphql_1.Int })),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Number]),
    __metadata("design:returntype", Promise)
], QuizzesResolver.prototype, "getFullQuiz", null);
__decorate([
    (0, graphql_1.Query)(returns => show_quiz_type_1.ShowQuizDTO),
    __param(0, (0, graphql_1.Args)('id', { type: () => graphql_1.Int })),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Number]),
    __metadata("design:returntype", Promise)
], QuizzesResolver.prototype, "showQuiz", null);
__decorate([
    (0, graphql_1.Query)(returns => reusult_type_1.ResultDTO),
    __param(0, (0, graphql_1.Args)('ReturnQuizDTO', { type: () => return_quiz_input_1.ReturnQuizDTO })),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [return_quiz_input_1.ReturnQuizDTO]),
    __metadata("design:returntype", Promise)
], QuizzesResolver.prototype, "checkQuiz", null);
exports.QuizzesResolver = QuizzesResolver = __decorate([
    (0, graphql_1.Resolver)(of => quiz_entity_1.Quiz),
    __metadata("design:paramtypes", [quizzes_service_1.QuizzesService])
], QuizzesResolver);
//# sourceMappingURL=quizzes.resolver.js.map