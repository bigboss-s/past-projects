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
exports.QuizzesService = void 0;
const common_1 = require("@nestjs/common");
const quiz_entity_1 = require("./quiz.entity");
const typeorm_1 = require("@nestjs/typeorm");
const typeorm_2 = require("typeorm");
const questions_entity_1 = require("../questions/questions.entity");
const answer_entity_1 = require("../answers/answer.entity");
const fastest_levenshtein_1 = require("fastest-levenshtein");
let QuizzesService = class QuizzesService {
    constructor(quizzesRepository, questionsRepository, answersRepository) {
        this.quizzesRepository = quizzesRepository;
        this.questionsRepository = questionsRepository;
        this.answersRepository = answersRepository;
    }
    async createQuiz(createQuizInput) {
        createQuizInput.questions.forEach(question => {
            const correctAnswersCount = question.answers.filter(answer => answer.isCorrect).length;
            const correctNotNullAnswersCount = question.answers.filter(answer => answer.isCorrect !== null).length;
            const orderedAnswerCount = question.answers.filter(answer => answer.order !== null).length;
            if ([questions_entity_1.QuestionType.MULTIPLE_ANSWER, questions_entity_1.QuestionType.SINGLE_ANSWER].includes(question.type)
                && correctNotNullAnswersCount !== question.answers.length) {
                throw new common_1.BadRequestException("All answers to single and multiple answer questions must be marked as correct or false");
            }
            if (question.type === questions_entity_1.QuestionType.MULTIPLE_ANSWER && correctAnswersCount === 0) {
                throw new common_1.BadRequestException("Multiple answer questions must have at least one correct answer");
            }
            if (question.type === questions_entity_1.QuestionType.SINGLE_ANSWER && correctAnswersCount !== 1) {
                throw new common_1.BadRequestException("Single answer question must have exactly one correct answer.");
            }
            if (question.type === questions_entity_1.QuestionType.OPEN_ANSWER && question.answers.length !== 1) {
                throw new common_1.BadRequestException("Plain text answer questions must have exactly one answer");
            }
            if (question.type === questions_entity_1.QuestionType.ORDERED_ANSWER && orderedAnswerCount !== question.answers.length) {
                throw new common_1.BadRequestException("All answers to a sorting question must be ordered");
            }
            if (question.type === questions_entity_1.QuestionType.ORDERED_ANSWER && !this.checkAnswerOrder(question.answers)) {
                throw new common_1.BadRequestException("Incorrect answer order. Ordering should start on 1 and increment by 1.");
            }
        });
        const quiz = this.quizzesRepository.create({
            name: createQuizInput.name,
            description: createQuizInput.description,
            questions: createQuizInput.questions.map(question => ({
                questionString: question.questionString,
                type: question.type,
                answers: question.answers.map(answer => ({
                    answerString: answer.answerString,
                    isCorrect: answer.isCorrect,
                    order: answer.order
                }))
            }))
        });
        return await this.quizzesRepository.save(quiz);
    }
    checkAnswerOrder(answers) {
        const orders = answers.map(answer => answer.order);
        orders.sort();
        for (let i = 0; i < orders.length; i++) {
            if (orders[i] !== i + 1) {
                return false;
            }
        }
        return true;
    }
    async findAll() {
        return await this.quizzesRepository.find({
            relations: ['questions', 'questions.answers']
        });
    }
    async findOne(quizId) {
        let quiz;
        try {
            quiz = await this.quizzesRepository.findOneOrFail({
                where: { id: quizId },
                relations: ['questions', 'questions.answers'],
            });
        }
        catch (EntityNotFoundError) {
            throw new common_1.BadRequestException('Quiz not found');
        }
        return quiz;
    }
    async findOneToShow(quizId) {
        const quiz = await this.findOne(quizId);
        const showQuiz = {
            id: quiz.id,
            name: quiz.name,
            description: quiz.description,
            questions: quiz.questions.map(question => {
                if (question.type !== questions_entity_1.QuestionType.OPEN_ANSWER) {
                    return {
                        id: question.id,
                        questionString: question.questionString,
                        type: question.type,
                        answers: question.answers.map(answer => ({
                            id: answer.id,
                            answerString: answer.answerString
                        })),
                    };
                }
                else {
                    return {
                        id: question.id,
                        questionString: question.questionString,
                        type: question.type
                    };
                }
            })
        };
        return showQuiz;
    }
    async checkQuiz(returnedQuiz) {
        let maxPoints = 0;
        let scoredPoints = 0;
        const quiz = await this.findOne(returnedQuiz.id);
        const questionMap = quiz.questions.reduce((acc, question) => {
            acc[question.id] = question.answers;
            return acc;
        }, {});
        for (const returnedQuestion of returnedQuiz.questions) {
            maxPoints++;
            switch (returnedQuestion.type) {
                case questions_entity_1.QuestionType.SINGLE_ANSWER:
                case questions_entity_1.QuestionType.MULTIPLE_ANSWER: {
                    const answerMap = {};
                    questionMap[returnedQuestion.id].forEach(question => {
                        answerMap[question.id] = question.isCorrect;
                    });
                    let isCorrect = true;
                    for (const returnedAnswer of returnedQuestion.answers) {
                        if (returnedAnswer.isCheckedTrue !== answerMap[returnedAnswer.id]) {
                            isCorrect = false;
                            break;
                        }
                    }
                    if (isCorrect)
                        scoredPoints++;
                    break;
                }
                case questions_entity_1.QuestionType.ORDERED_ANSWER: {
                    const answerMap = {};
                    questionMap[returnedQuestion.id].forEach(question => {
                        answerMap[question.id] = question.order;
                    });
                    let isCorrect = true;
                    for (const returnedAnswer of returnedQuestion.answers) {
                        if (returnedAnswer.checkedOrder !== answerMap[returnedAnswer.id]) {
                            isCorrect = false;
                            break;
                        }
                    }
                    if (isCorrect)
                        scoredPoints++;
                    break;
                }
                case questions_entity_1.QuestionType.OPEN_ANSWER: {
                    const correctAnswer = questionMap[returnedQuestion.id][0].answerString;
                    if ((0, fastest_levenshtein_1.distance)(correctAnswer, returnedQuestion.answers[0].answerString) < 3) {
                        scoredPoints++;
                    }
                    else {
                    }
                    break;
                }
            }
        }
        return {
            scoredPoints: scoredPoints,
            maxPoints: maxPoints
        };
    }
};
exports.QuizzesService = QuizzesService;
exports.QuizzesService = QuizzesService = __decorate([
    (0, common_1.Injectable)(),
    __param(0, (0, typeorm_1.InjectRepository)(quiz_entity_1.Quiz)),
    __param(1, (0, typeorm_1.InjectRepository)(questions_entity_1.Question)),
    __param(2, (0, typeorm_1.InjectRepository)(answer_entity_1.Answer)),
    __metadata("design:paramtypes", [typeorm_2.Repository,
        typeorm_2.Repository,
        typeorm_2.Repository])
], QuizzesService);
//# sourceMappingURL=quizzes.service.js.map