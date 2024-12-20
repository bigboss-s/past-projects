import { Field, Int, ObjectType } from "@nestjs/graphql";
import { Question } from "../questions/questions.entity";
import { Column, Entity, ManyToOne, PrimaryGeneratedColumn } from "typeorm";

@Entity()
@ObjectType()
export class Answer{
    
    @PrimaryGeneratedColumn()
    @Field(type => Int)
    id: number;

    @Column()
    @Field()
    answerString: string;

    @Column({nullable: true})
    @Field({nullable: true})
    isCorrect?: boolean;

    @Column({nullable: true})
    @Field({nullable: true})
    order?: number;

    @Column()
    @Field(type => Int)
    questionId: number;

    @ManyToOne(() => Question, question => question.answers)
    @Field(type => Question)
    question: Question;

}