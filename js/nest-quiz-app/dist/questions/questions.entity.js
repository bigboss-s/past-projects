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
exports.Question = exports.QuestionType = void 0;
const graphql_1 = require("@nestjs/graphql");
const answer_entity_1 = require("../answers/answer.entity");
const quiz_entity_1 = require("../quizzes/quiz.entity");
const typeorm_1 = require("typeorm");
var QuestionType;
(function (QuestionType) {
    QuestionType["SINGLE_ANSWER"] = "SINGLE_ANSWER";
    QuestionType["MULTIPLE_ANSWER"] = "MULTIPLE_ANSWER";
    QuestionType["ORDERED_ANSWER"] = "ORDERED_ANSWER";
    QuestionType["OPEN_ANSWER"] = "OPEN_ANSWER";
})(QuestionType || (exports.QuestionType = QuestionType = {}));
(0, graphql_1.registerEnumType)(QuestionType, {
    name: 'QuestionType',
});
let Question = class Question {
};
exports.Question = Question;
__decorate([
    (0, typeorm_1.PrimaryGeneratedColumn)(),
    (0, graphql_1.Field)(type => graphql_1.Int),
    __metadata("design:type", Number)
], Question.prototype, "id", void 0);
__decorate([
    (0, typeorm_1.Column)(),
    (0, graphql_1.Field)(),
    __metadata("design:type", String)
], Question.prototype, "questionString", void 0);
__decorate([
    (0, typeorm_1.Column)({
        type: 'enum',
        enum: QuestionType,
        default: QuestionType.SINGLE_ANSWER
    }),
    (0, graphql_1.Field)(type => QuestionType),
    __metadata("design:type", String)
], Question.prototype, "type", void 0);
__decorate([
    (0, typeorm_1.Column)(),
    (0, graphql_1.Field)(type => graphql_1.Int),
    __metadata("design:type", Number)
], Question.prototype, "quizId", void 0);
__decorate([
    (0, typeorm_1.ManyToOne)(() => quiz_entity_1.Quiz, quiz => quiz.questions),
    (0, graphql_1.Field)(type => quiz_entity_1.Quiz),
    __metadata("design:type", quiz_entity_1.Quiz)
], Question.prototype, "quiz", void 0);
__decorate([
    (0, typeorm_1.OneToMany)(() => answer_entity_1.Answer, answer => answer.question, {
        cascade: true
    }),
    (0, graphql_1.Field)(type => [answer_entity_1.Answer]),
    __metadata("design:type", Array)
], Question.prototype, "answers", void 0);
exports.Question = Question = __decorate([
    (0, typeorm_1.Entity)(),
    (0, graphql_1.ObjectType)()
], Question);
//# sourceMappingURL=questions.entity.js.map